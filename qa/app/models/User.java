package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import models.helpers.ICleanup;
import models.helpers.IFilter;
import models.helpers.IObservable;
import models.helpers.IObserver;
import models.helpers.Mapper;
import models.helpers.Tools;

/**
 * A user with a name. Can contain {@link Item}s i.e. {@link Question}s,
 * {@link Answer}s, {@link Comment}s and {@link Vote}s. When deleted, the
 * <code>User</code> requests all his {@link Item}s to delete themselves.
 * 
 * Furthermore, they have their own profile with informations about them,
 * an email address to contact them and some more attributes and methods that
 * represent the user of the system and their actions.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class User implements IObserver, ICleanup<Item> {

	private final String name;
	private String password;
	private String email;
	private final HashSet<Item> items;
	private String fullname;
	protected Date dateOfBirth;
	private String website;
	private String profession;
	private String employer;
	private String biography;

	private String confirmKey;

	private String statustext = "";
	private boolean isBlocked = false;
	private boolean isModerator = false;
	private boolean isConfirmed = false;
	private long lastSearch = 0;
	private String lastSearchTerm = "";
	private long lastPost = 0;

	private final Mailbox mainMailbox;
	private IMailbox moderatorMailbox;
	private boolean isSpammer;
	private final ICleanup<User> cleaner;

	/**
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 * @param password
	 *            the user's password
	 * @param email
	 *            the user's valid(!) email address
	 * @param cleaner
	 *            an optional clean-up object that wants to be notified when
	 *            this user object is no longer needed
	 */
	public User(String name, String password, String email,
			ICleanup<User> cleaner) {
		this.name = name;
		if (password != null) {
			// null passwords may happen during testing, as hashing a password
			// can be quite slow in comparison
			this.password = Tools.encrypt(password);
		}
		this.email = email;
		this.confirmKey = Tools.randomStringGenerator(35);
		this.items = new HashSet<Item>();
		this.mainMailbox = new Mailbox(name);
		this.isSpammer = false;
		this.cleaner = cleaner;
	}

	/**
	 * Only for tests:
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 */
	public User(String name) {
		this(name, null, null, null);
	}

	public boolean canEdit(Entry entry) {
		return entry.owner() == this && !this.isBlocked() || this.isModerator();
	}

	/**
	 * Gets the name of the <code>User</code>.
	 * 
	 * @return name of the <code>User</code>
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Encrypt the password and check if it is the same as the stored one.
	 * 
	 * @param passwort
	 * @return true if the password is right
	 */
	public boolean checkPW(String password) {
		return this.password.equals(Tools.encrypt(password));
	}

	/**
	 * Registers an {@link Item} which should be deleted in case the
	 * <code>User</code> gets deleted and, if the item is an {@link Entry},
	 * remembers the time of the user's last post for spamming prevention.
	 * 
	 * @param item
	 *            the {@link Item} to register
	 */
	public void registerItem(Item item) {
		this.items.add(item);
		this.updateCheaterStatus();
		if (item instanceof Entry) {
			this.setLastPostTime(SysInfo.now());
		}
	}

	/**
	 * Causes the <code>User</code> to delete all his {@link Item}s.
	 */
	public void delete() {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone) {
			item.delete();
		}
		this.items.clear();
		this.mainMailbox.delete();
		if (this.cleaner != null) {
			this.cleaner.cleanUp(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.helpers.ICleanup#cleanUp(java.lang.Object)
	 */
	public void cleanUp(Item item) {
		this.items.remove(item);
	}

	/**
	 * Checks if an {@link Item} is registered and therefore owned by a
	 * <code>User</code>.
	 * 
	 * @param item
	 *            the {@link Item}to check
	 * @return true if the {@link Item} is registered
	 */
	public boolean hasItem(Item item) {
		return this.items.contains(item);
	}

	/**
	 * The amount of Comments, Answers and Questions the <code>User</code> has
	 * posted in the last 60 Minutes.
	 * 
	 * @return The amount of Comments, Answers and Questions for this
	 *         <code>User</code> in this Hour.
	 */
	public int howManyItemsPerHour() {
		long now = SysInfo.now().getTime();
		int i = 0;
		for (Item item : this.items) {
			if (now - item.timestamp().getTime() <= 60 * 60 * 1000) {
				i++;
			}
		}
		return i;
	}

	/**
	 * The <code>User</code> is a Cheater if over 50% of his votes is for the
	 * same <code>User</code>.
	 * 
	 * @return True if the <code>User</code> is supporting somebody.
	 */
	public boolean isMaybeCheater() {
		if (SysInfo.isInTestMode())
			return false;

		int voteCount = 0;
		HashMap<User, Integer> votesForUser = new HashMap<User, Integer>();
		for (Item item : this.items) {
			if (item instanceof Vote && ((Vote) item).up()) {
				Vote vote = (Vote) item;
				Integer count = votesForUser.get(vote.getEntry().owner());
				if (count == null) {
					count = 0;
				}
				votesForUser.put(vote.getEntry().owner(), count + 1);
				voteCount++;
			}
		}

		if (votesForUser.isEmpty())
			return false;

		Integer maxCount = Collections.max(votesForUser.values());
		return maxCount > 3 && maxCount > 0.5 * voteCount;
	}

	/**
	 * Anonymizes all questions, answers and comments by this user.
	 * 
	 * @param keepOnlyQuestions
	 *            whether to anonymize this user's answers and comments as well
	 *            or whether to just keep his/her questions
	 */
	public void anonymize(boolean keepOnlyQuestions) {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone) {
			if (item instanceof Question || keepOnlyQuestions
					&& item instanceof Entry) {
				((Entry) item).anonymize();
				this.items.remove(item);
			}
		}
	}

	/**
	 * The <code>User</code> is a Spammer if he posts more than 60 comments,
	 * answers or questions in the last hour.
	 * 
	 * @return True if the <code>User</code> is a Spammer.
	 */
	public boolean isSpammer() {
		if (SysInfo.isInTestMode())
			return false;
		if (this.isSpammer)
			return true;
		int number = this.howManyItemsPerHour();
		if (number >= 60)
			return true;
		return false;
	}

	/**
	 * A <code>User</code> is a Cheater when he spams the Site or supports
	 * somebody.
	 * 
	 * @return true if <code>User</code> is a Spammer or supports somebody.
	 * 
	 */
	public boolean isCheating() {
		return this.isSpammer() || this.isMaybeCheater();
	}

	/**
	 * Blocks the User if he is a cheater or unblocks him if he is not cheating.
	 * The Cheater gets the appropriate status message.
	 * 
	 * This method is supposed to be called after each new post of this user, as
	 * we will remember here the time of the user's last post for future
	 * spamming prevention.
	 */
	private void updateCheaterStatus() {
		if (this.isSpammer()) {
			this.block("User is a Spammer");
		} else if (this.isMaybeCheater()) {
			this.block("User voted up somebody");
		}
	}

	/**
	 * Calculates the age of the <code>User</code> in years.
	 * 
	 * @return age of the <code>User</code>
	 */
	private int age() {
		if (this.dateOfBirth != null) {
			long age = SysInfo.now().getTime() - this.dateOfBirth.getTime();
			return (int) (age / ((long) 1000 * 3600 * 24 * 365));
		} else
			return 0;
	}

	/* Getter and Setter for profile data */

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setDateOfBirth(String birthday) throws ParseException {
		this.dateOfBirth = Tools.stringToDate(birthday);
	}

	public String getDateOfBirth() {
		return Tools.dateToString(this.dateOfBirth);
	}

	public int getAge() {
		return this.age();
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getProfession() {
		return this.profession;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getEmployer() {
		return this.employer;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public String getBiography() {
		return this.biography;
	}

	public String getBiographyHTML() {
		if (this.biography == null)
			return null;
		return Tools.markdownToHtml(this.biography);
	}

	public String getSHA1Password() {
		return this.password;
	}

	public void setSHA1Password(String password) {
		this.password = Tools.encrypt(password);
	}

	public String getConfirmKey() {
		return this.confirmKey;
	}

	public void setConfirmKey(String key) {
		this.confirmKey = key;
	}

	/**
	 * Get the reason for why the user is blocked.
	 * 
	 * @return the reason
	 */
	public String getStatusMessage() {
		return this.statustext;
	}

	/**
	 * Blocks a <code>User</code> and gives him the reason.
	 * 
	 * @param block
	 *            , true if the user has to be blocked
	 * @param reason
	 *            , why the users is getting blocked.
	 */
	public void block(String reason) {
		this.isBlocked = true;
		this.statustext = reason;
	}

	/**
	 * Cleans the name of the User. They are no longer accused of any spamming
	 * or any other thing causing them to be blocked.
	 */
	public void unblock() {
		this.isBlocked = false;
		this.isSpammer = false;
		this.statustext = "";
	}

	/**
	 * Get the current status of the user whether he is blocked or not.
	 * 
	 * @return true, if the user is blocked
	 */
	public boolean isBlocked() {
		return this.isBlocked;
	}

	/**
	 * Get the status of the user whether he is a moderator or not.
	 * 
	 * @return true, if the user is moderator
	 */
	public boolean isModerator() {
		return this.isModerator;
	}

	/**
	 * Get the status of the user if he is confirmed or not.
	 * 
	 * @return true, if the user is confirmed
	 */
	public boolean isConfirmed() {
		return this.isConfirmed;
	}

	/**
	 * Set the status of the user whether he is a moderator or not.
	 * 
	 * @param mod
	 *            whether the user is to become a moderator or not
	 * @param mailbox
	 *            an optional moderator mailbox, through which the user will be
	 *            notified about spam reports, etc.
	 */
	public void setModerator(boolean mod, IMailbox mailbox) {
		this.isModerator = mod;
		this.moderatorMailbox = mod ? mailbox : null;
	}

	/**
	 * Set the status of the user on confirmed
	 */
	public void confirm() {
		this.isConfirmed = true;
	}

	/**
	 * Start observing changes for an entry (e.g. new answers to a question).
	 * 
	 * @param what
	 *            the entry to watch
	 */
	public void startObserving(IObservable what) {
		what.addObserver(this);
	}

	/**
	 * Checks if a specific entry is being observed for changes.
	 * 
	 * @param what
	 *            the entry to check
	 */
	public boolean isObserving(IObservable what) {
		return what.hasObserver(this);
	}

	/**
	 * Stop observing changes for an entry (e.g. new answers to a question).
	 * 
	 * @param what
	 *            the entry to unwatch
	 */
	public void stopObserving(IObservable what) {
		what.removeObserver(this);
	}

	/**
	 * Observe new answers added to questions this user is observing (obviously
	 * ignore answers given by this very user, though).
	 * 
	 * @see models.IObserver#observe(models.IObservable, java.lang.Object)
	 */
	public void observe(IObservable o, Object arg) {
		if (o instanceof Question && arg instanceof Answer
				&& ((Answer) arg).owner() != this) {
			this.mainMailbox.notify(this, (Answer) arg);
		}
	}

	/**
	 * Get a List of the last three <code>Question</code>s of this
	 * <code>User</code>. Registers a new <code>User</code> to the database.
	 * 
	 * @param username
	 * @param password
	 *            of the <code>User</code>
	 * @return user
	 */
	public List<Question> getRecentQuestions() {
		return this.getRecentItemsByType(Question.class);
	}

	/**
	 * Get a list of all Questions the user has answered sorted by how high the
	 * answers are rated.
	 * 
	 * @return List<Question>
	 */
	public List<Question> getSortedAnsweredQuestions() {
		List<Question> sortedAnsweredQuestions = new ArrayList<Question>();
		/*
		 * Get all questions the user has answered. Ignore duplicates. Don't add
		 * those questions belonging to negative rated answers.
		 */
		// getAnswers already sorts all answers - best first
		for (Answer a : this.getAnswers()) {
			Question q = a.getQuestion();
			if (!sortedAnsweredQuestions.contains(q) && a.rating() >= 0) {
				sortedAnsweredQuestions.add(q);
			}
		}

		return sortedAnsweredQuestions;
	}

	/**
	 * Get a List of the last three <code>Answer</code>s of this
	 * <code>User</code>.
	 * 
	 * @return List<Answer> The last three <code>Answer</code>s of this
	 *         <code>User</code>
	 */
	public List<Answer> getRecentAnswers() {
		return this.getRecentItemsByType(Answer.class);
	}

	/**
	 * Get a List of the last three <code>Comment</code>s of this
	 * <code>User</code>.
	 * 
	 * @return List<Comment> The last three <code>Comment</code>s of this
	 *         <code>User</code>
	 */
	public List<Comment> getRecentComments() {
		return this.getRecentItemsByType(Comment.class);
	}

	/**
	 * Get a List of the last three <code>Items</code>s of type T of this
	 * <code>User</code>.
	 * 
	 * @return List<Item> The last three <code>Item</code>s of this
	 *         <code>User</code>
	 */
	protected List getRecentItemsByType(Class type) {
		List recentItems = this.getItemsByType(type);
		Collections.sort(recentItems, new Comparator<Item>() {
			public int compare(Item i1, Item i2) {
				return i2.timestamp().compareTo(i1.timestamp());
			}
		});
		if (recentItems.size() > 3)
			return recentItems.subList(0, 3);
		return recentItems;
	}

	/**
	 * Get a sorted ArrayList of all <code>Questions</code>s of this
	 * <code>User</code>.
	 * 
	 * @return ArrayList<Question> All questions of this <code>User</code>
	 */
	public List<Question> getQuestions() {
		return this.getItemsByType(Question.class);
	}

	/**
	 * Get a sorted ArrayList of all <code>Answer</code>s of this
	 * <code>User</code>.
	 * 
	 * @return ArrayList<Answer> All <code>Answer</code>s of this
	 *         <code>User</code>
	 */
	public List<Answer> getAnswers() {
		return this.getItemsByType(Answer.class);
	}

	/**
	 * Get a sorted ArrayList of all <code>Comment</code>s of this
	 * <code>User</code>
	 * 
	 * @return ArrayList<Comment> All <code>Comments</code>s of this
	 *         <code>User</code>
	 */
	public List<Comment> getComments() {
		return this.getItemsByType(Comment.class);
	}

	/**
	 * Get a List of all best rated answers
	 * 
	 * @return List<Answer> All best rated answers
	 */
	public List<Answer> bestAnswers() {
		return Mapper.filter(this.getAnswers(), new IFilter<Answer, Boolean>() {
			public Boolean visit(Answer a) {
				return a.isBestAnswer();
			}
		});
	}

	/**
	 * Get an ArrayList of all highRated answers
	 * 
	 * @return List<Answer> All high rated answers
	 */
	public List<Answer> highRatedAnswers() {
		return Mapper.filter(this.getAnswers(), new IFilter<Answer, Boolean>() {
			public Boolean visit(Answer a) {
				return a.isHighRated();
			}
		});
	}

	public List<Notification> getNotifications() {
		List<Notification> all = new LinkedList();
		for (IMailbox mailbox : this.getAllMailboxes()) {
			all.addAll(mailbox.getAllNotifications());
		}
		return all;
	}

	/**
	 * Gets the most recent unread notification, if there is any very recent one
	 * 
	 * @return a very recent notification (or null, if there isn't any)
	 */
	public Notification getVeryRecentNewNotification() {
		for (Notification n : this.mainMailbox.getNewNotifications())
			if (n.isVeryRecent())
				return n;
		return null;
	}

	/**
	 * Gets a notification by its id value.
	 * 
	 * NOTE: slightly hacky since we don't track notifications in a separate
	 * HashMap but in this.items like everything else - this should get fixed
	 * once we migrate to using a real DB.
	 * 
	 * @param id
	 *            the notification's id
	 * @return a notification with the given id
	 */
	public Notification getNotification(int id) {
		for (Notification n : this.getNotifications())
			if (n.id() == id)
				return n;
		return null;
	}

	/**
	 * Get an ArrayList of all items of this user being an instance of a
	 * specific type.
	 * 
	 * @param type
	 *            the type
	 * @return ArrayList All type-items of this user
	 */
	protected List getItemsByType(Class type) {
		List items = new ArrayList();
		for (Item item : this.items)
			if (type.isInstance(item)) {
				items.add(item);
			}
		Collections.sort(items);
		return items;
	}

	public void setDateOfBirth(Date time) {
		this.dateOfBirth = time;
	}

	@Override
	public String toString() {
		return "U[" + this.name + "]";
	}

	/**
	 * Remembers the term a user last searched for and the time of the search so
	 * that we can specifically permit the user to continue a specific search
	 * (e.g. display the next batch of search results) while still preventing
	 * the user from starting a new search too soon after the last one.
	 * 
	 * @param term
	 *            the term a user has last searched for
	 * @param time
	 *            the date/time of the last search
	 */
	public void setLastSearch(String term, Date time) {
		this.lastSearchTerm = term;
		this.lastSearch = time.getTime();
	}

	/**
	 * Checks if the user can use the search for a specific term. There must be
	 * at least 15 seconds between his last search and now, if it's a different
	 * search.
	 * 
	 * @return true if the user can search
	 */
	public boolean canSearchFor(String term) {
		return SysInfo.isInTestMode()
				|| term.equals(this.lastSearchTerm) || this.timeToSearch() <= 0;
	}

	/**
	 * Calculates the remaining time until the user can make a new search.
	 * Counting down from 15.
	 * 
	 * @return an Integer that equals the remaining seconds.
	 */
	public int timeToSearch() {
		return (int) (15 - (SysInfo.now().getTime() - this.lastSearch) / 1000);
	}

	/**
	 * Set the time of the Users last Post to a specific one.
	 * 
	 * @param Time
	 *            in milliseconds after 1970.
	 */
	public void setLastPostTime(Date time) {
		this.lastPost = time.getTime();
	}

	/**
	 * Checks if the user can ask, answer or comment a post. There must be at
	 * least 30 seconds between his last post to make a new one and he must not
	 * be blocked.
	 * 
	 * @return true if the user can post
	 */
	public boolean canPost() {
		return SysInfo.isInTestMode() || !this.isBlocked()
				&& this.timeToPost() <= 0;
	}

	/**
	 * Calculates the remaining time until he can make a new post. Counting down
	 * from 30.
	 * 
	 * @return an Integer that equals the remaining seconds.
	 */
	public int timeToPost() {
		return (int) (30 - (SysInfo.now().getTime() - this.lastPost) / 1000);
	}

	public List<IMailbox> getAllMailboxes() {
		List<IMailbox> mailboxes = new ArrayList();
		mailboxes.add(this.mainMailbox);
		if (this.isModerator())
			mailboxes.add(this.moderatorMailbox);
		return mailboxes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getNew()
	 */
	public List<Notification> getNewNotifications() {
		List<Notification> allNew = new LinkedList();
		for (IMailbox mailbox : this.getAllMailboxes()) {
			allNew.addAll(mailbox.getNewNotifications());
		}
		return allNew;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see models.IMailbox#getRecent()
	 */
	public List<Notification> getRecentNotifications() {
		List<Notification> allRecent = new LinkedList();
		for (IMailbox mailbox : this.getAllMailboxes()) {
			allRecent.addAll(mailbox.getRecentNotifications());
		}
		return allRecent;
	}

	public void setIsSpammer(boolean isSpammer) {
		if (isSpammer && !this.isBlocked) {
			this.block("Declared Spammer");
		}
		this.isSpammer = isSpammer;
	}
}
