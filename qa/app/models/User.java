package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import models.database.Database;
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
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class User implements IObserver {

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

	/**
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 */
	public User(String name, String password, String email) {
		this.name = name;
		this.password = Tools.encrypt(password);
		this.email = email;
		this.confirmKey = Tools.randomStringGenerator(35);
		this.items = new HashSet<Item>();
	}
	
	/**
	 * Only for tests: 
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 */
	public User(String name, String password) {
		this(name, password, null);
	}

	public boolean canEdit(Entry entry) {
		return entry.owner() == this && !isBlocked() || isModerator();
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
	 * <code>User</code> gets deleted.
	 * 
	 * @param item
	 *            the {@link Item} to register
	 */
	public void registerItem(Item item) {
		this.items.add(item);
		updateCheaterStatus();
	}

	/**
	 * Causes the <code>User</code> to delete all his {@link Item}s.
	 */
	public void delete() {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone) {
			item.unregister();
		}
		this.items.clear();
		Database.get().users().remove(this.name);
	}

	/**
	 * Unregisters an {@link Item} which has been deleted.
	 * 
	 * @param item
	 *            the {@link Item} to unregister
	 */
	public void unregister(Item item) {
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
		Date now = SystemInformation.get().now();
		int i = 0;
		for (Item item : this.items) {
			if (now.getTime() - item.timestamp().getTime() <= 60 * 60 * 1000) {
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
	 * The <code>User</code> is a Spammer if he posts more than 30 comments,
	 * answers or questions in the last hour.
	 * 
	 * @return True if the <code>User</code> is a Spammer.
	 */
	public boolean isSpammer() {
		int number = howManyItemsPerHour();
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
		return isSpammer() || isMaybeCheater();
	}

	/**
	 * Blocks the User if he is a cheater or unblocks him if he is not cheating.
	 * The Cheater gets the appropriate status message.
	 * 
	 * This method is supposed to be called after each new post of this user, as
	 * we will remember here the time of the user's last post for future
	 * spamming prevention.
	 */
	public void updateCheaterStatus() {
		if (isSpammer()) {
			block("User is a Spammer");
		} else if (isMaybeCheater()) {
			block("User voted up somebody");
		}
		this.setLastPostTime(SystemInformation.get().now());
	}

	/**
	 * Calculates the age of the <code>User</code> in years.
	 * 
	 * @return age of the <code>User</code>
	 */
	private int age() {
		Date now = SystemInformation.get().now();
		if (this.dateOfBirth != null) {
			long age = now.getTime() - this.dateOfBirth.getTime();
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
		return age();
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

	public void unblock() {
		this.isBlocked = false;
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
	 * @return true, if the user is confirmed
	 */
	public boolean isConfirmed() {
		return this.isConfirmed;
	}

	/**
	 * Set the status of the user whether he is a moderator or not.
	 * 
	 * @param mod
	 *            , true if the user will be a moderator
	 */
	public void setModerator(Boolean mod) {
		this.isModerator = mod;
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
	 * @see models.IObserver#observe(models.IObservable, java.lang.Object)
	 */
	public void observe(IObservable o, Object arg) {
		if (o instanceof Question && arg instanceof Answer
				&& ((Answer) arg).owner() != this) {
			new Notification(this, (Answer) arg);
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
		return getRecentItemsByType(Question.class);
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
		for (Answer a : getAnswers()) {
			Question q = a.getQuestion();
			if (!sortedAnsweredQuestions.contains(q) && a.rating() >= 0) {
				sortedAnsweredQuestions.add(q);
			}
		}

		return sortedAnsweredQuestions;
	}

	/**
	 * Get a list of all questions that the user might also know to answer.
	 * 
	 * @return List<Question>
	 */
	public List<Question> getSuggestedQuestions() {
		List<Question> suggestedQuestions = new ArrayList<Question>();
		List<Question> sortedAnsweredQuestions = getSortedAnsweredQuestions();

		/*
		 * Don't list questions that have many answers or already have a best
		 * answer. The user should not be the owner of the suggested question.
		 * Remove duplicates.
		 */
		for (Question q : sortedAnsweredQuestions) {
			for (Question similarQ : q.getSimilarQuestions()) {
				if (!suggestedQuestions.contains(similarQ)
						&& !sortedAnsweredQuestions.contains(similarQ)
						&& similarQ.owner() != this
						&& !similarQ.isOldQuestion()
						&& similarQ.countAnswers() < 10
						&& !similarQ.hasBestAnswer()) {
					suggestedQuestions.add(similarQ);
				}
			}
		}
		if (suggestedQuestions.size() > 6)
			return suggestedQuestions.subList(0, 6);
		return suggestedQuestions;

	}

	/**
	 * Get a List of the last three <code>Answer</code>s of this
	 * <code>User</code>.
	 * 
	 * @return List<Answer> The last three <code>Answer</code>s of this
	 *         <code>User</code>
	 */
	public List<Answer> getRecentAnswers() {
		return getRecentItemsByType(Answer.class);
	}

	/**
	 * Get a List of the last three <code>Comment</code>s of this
	 * <code>User</code>.
	 * 
	 * @return List<Comment> The last three <code>Comment</code>s of this
	 *         <code>User</code>
	 */
	public List<Comment> getRecentComments() {
		return getRecentItemsByType(Comment.class);
	}

	/**
	 * Get a List of the last three <code>Items</code>s of type T of this
	 * <code>User</code>.
	 * 
	 * @return List<Item> The last three <code>Item</code>s of this
	 *         <code>User</code>
	 */
	protected List getRecentItemsByType(Class type) {
		List recentItems = getItemsByType(type);
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
		return getItemsByType(Question.class);
	}

	/**
	 * Get a sorted ArrayList of all <code>Answer</code>s of this
	 * <code>User</code>.
	 * 
	 * @return ArrayList<Answer> All <code>Answer</code>s of this
	 *         <code>User</code>
	 */
	public List<Answer> getAnswers() {
		return getItemsByType(Answer.class);
	}

	/**
	 * Get a sorted ArrayList of all <code>Comment</code>s of this
	 * <code>User</code>
	 * 
	 * @return ArrayList<Comment> All <code>Comments</code>s of this
	 *         <code>User</code>
	 */
	public List<Comment> getComments() {
		return getItemsByType(Comment.class);
	}

	/**
	 * Get a List of all best rated answers
	 * 
	 * @return List<Answer> All best rated answers
	 */
	public List<Answer> bestAnswers() {
		return Mapper.filter(getAnswers(), new IFilter<Answer, Boolean>() {
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
		return Mapper.filter(getAnswers(), new IFilter<Answer, Boolean>() {
			public Boolean visit(Answer a) {
				return a.isHighRated();
			}
		});
	}

	/**
	 * Get an ArrayList of all notifications of this user, sorted most-recent
	 * one first and optionally fulfilling one filter criterion.
	 * 
	 * @param filter
	 *            an optional name of a filter method (e.g. "isNew")
	 * @return ArrayList<Notification> All notifications of this user
	 */
	protected List<Notification> getAllNotifications() {
		ArrayList<Notification> result = new ArrayList<Notification>();
		/*
		 * Hack: remove all notifications to deleted answers
		 * 
		 * unfortunately, there's currently no other way to achieve this, as
		 * there is no global list of all existing notifications nor an easy way
		 * to register all users for observing the deletion of answers (because
		 * there's no global list of all existing users, either)
		 */
		List<Notification> notifications = getItemsByType(Notification.class);
		for (Notification n : notifications) {
			// currently, we only notify about answers
			Answer answer = (Answer) n.getAbout();
			if (answer.getQuestion() != null) {
				result.add(n);
			} else {
				n.unregister();
			}
		}
		return result;
	}

	/**
	 * Get an ArrayList of all notifications of this user, sorted most-recent
	 * one first.
	 * 
	 * @return ArrayList<Notification> All notifications of this user
	 */
	public List<Notification> getNotifications() {
		return getAllNotifications();
	}

	/**
	 * Get an ArrayList of all unread notifications of this user
	 * 
	 * @return the unread notifications
	 */
	public List<Notification> getNewNotifications() {
		return Mapper.filter(getAllNotifications(),
				new IFilter<Notification, Boolean>() {
					public Boolean visit(Notification n) {
						return n.isNew();
					}
				});
	}

	/**
	 * Gets the most recent unread notification, if there is any very recent one
	 * 
	 * @return a very recent notification (or null, if there isn't any)
	 */
	public Notification getVeryRecentNewNotification() {
		for (Notification n : getNewNotifications())
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
		for (Notification n : getNotifications())
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
	 * Having less than MINIMAL_EXPERTISE_THRESHOLD votes on a topic prevents a
	 * user from being an expert.
	 */
	private final int MINIMAL_EXPERTISE_THRESHOLD = 2;
	/**
	 * What percentage of most proficient users are considered experts on a
	 * topic.
	 */
	private final int EXPERTISE_PERCENTILE = 20;

	/**
	 * Determines all the topics this user is an expert in. Each tag is
	 * considered a topic and a user is considered an expert if he's got a
	 * minimum of two positive votes (or one accepted best answer) to one of the
	 * questions with the tag and if his vote count is in the first quintile
	 * (i.e. at most 20 % of all the answerers for a given topic can be
	 * experts).
	 * 
	 * @return the list of tags for which this user is an expert
	 */
	public List<Tag> getExpertise() {
		Map<Tag, Map<User, Integer>> stats = Database.get().questions()
				.collectExpertiseStatistics();

		List<Tag> expertise = new ArrayList();
		for (Tag tag : stats.keySet()) {
			// ignore tags this user knows nothing about
			if (!stats.get(tag).containsKey(this)) {
				continue;
			}
			// ignore tags this user knows hardly anything about
			if (stats.get(tag).get(this) < MINIMAL_EXPERTISE_THRESHOLD) {
				continue;
			}

			Map<User, Integer> tagStats = stats.get(tag);
			List<User> experts = Mapper.sortByValue(tagStats);
			int threshold = (100 - EXPERTISE_PERCENTILE) * experts.size() / 100;
			if (tagStats.get(this) >= tagStats.get(experts.get(threshold))) {
				expertise.add(tag);
			}
		}

		return expertise;
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
		return term.equals(this.lastSearchTerm) || this.timeToSearch() <= 0;
	}

	/**
	 * Calculates the remaining time until the user can make a new search.
	 * Counting down from 15.
	 * 
	 * @return an Integer that equals the remaining seconds.
	 */
	public int timeToSearch() {
		return (int) (15 - (SystemInformation.get().now().getTime() - this.lastSearch) / 1000);
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
		return !this.isBlocked() && this.timeToPost() <= 0;
	}

	/**
	 * Calculates the remaining time until he can make a new post. Counting down
	 * from 30.
	 * 
	 * @return an Integer that equals the remaining seconds.
	 */
	public int timeToPost() {
		return (int) (30 - (SystemInformation.get().now().getTime() - this.lastPost) / 1000);
	}

}
