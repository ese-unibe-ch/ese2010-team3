package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A user with a name. Can contain {@link Item}s i.e. {@link Question}s,
 * {@link Answer}s, {@link Comment}s and {@link Vote}s. When deleted, the
 * <code>User</code> requests all his {@link Item}s to delete themselves.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 * 
 */
public class User {

	private final String name;
	private final String password;
	private String email;
	private final HashSet<Item> items;
	private String fullname;
	protected Date dateOfBirth;
	private String website;
	private String profession;
	private String employer;
	private String biography;

	public static final String DATE_FORMAT = "dd-MM-yy";

	private static HashMap<String, User> users = new HashMap();

	/**
	 * Creates a <code>User</code> with a given name.
	 * @param name the name of the <code>User</code>
	 */
	public User(String name, String password) {
		this.name = name;
		this.password = password;
		this.items = new HashSet<Item>();
		users.put(name, this);
	}

	/**
	 * Returns the name of the <code>User</code>.
	 * @return name of the <code>User</code>
	 */
	public String name() {
		return this.name;
	}

	public boolean checkPW(String password){
		return this.password.equals(password);
	}
	
	public static boolean needSignUp(String username){
    	return (User.get(username)==null);
    }
	
	public static User register(String username, String password) {
		return new User(username, password);
	}
	
	public boolean checkeMail(String email){
		return this.email.equals(email);
	}

	/**
	 * Registers an {@link Item} which should be deleted in case the <code>User</code> gets deleted.
	 * 
	 * @param item the {@link Item} to register
	 */
	public void registerItem(Item item) {
		this.items.add(item);
	}

	/*
	 * Causes the <code>User</code> to delete all his {@link Item}s.
	 */
	public void delete() {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone)
			item.unregister();
		this.items.clear();
		users.remove(this.name);
	}

	/**
	 * Unregisters an {@link Item} which has been deleted.
	 * @param item the {@link Item} to unregister
	 */
	public void unregister(Item item) {
		this.items.remove(item);
	}

	/**
	 * Checks if an {@link Item} is registered and therefore owned by a
	 * <code>User</code>.
	 * @param item the {@link Item}to check
	 * @return true if the {@link Item} is registered
	 */
	public boolean hasItem(Item item) {
		return this.items.contains(item);
	}

	/**
	 * Get the <code>User</code> with the given name.
	 * @param name
	 * @return a <code>User</code> or null if the given name doesn't exist.
	 */
	public static User get(String name) {
		if (users.containsKey(name))
			return users.get(name);
		return null;
	}

	/**
	 * Anonymizes all questions, answers and comments by this user.
	 * 
	 * @param doAnswers
	 *            - whether to anonymize this user's answers as well
	 * @param doComments
	 *            - whether to anonymize this user's comments as well
	 */
	public void anonymize(boolean doAnswers, boolean doComments) {
		// operate on a clone to prevent a ConcurrentModificationException
		HashSet<Item> clone = (HashSet<Item>) this.items.clone();
		for (Item item : clone) {
			if (item instanceof Question || doAnswers && item instanceof Answer
					|| doComments && item instanceof Comment) {
				((Entry) item).anonymize();
				this.items.remove(item);
			}
		}
	}

	/**
	 * Calculates the age of the <code>User</code> in years
	 * 
	 * @return age of the <code>User</code>
	 */
	private int age() {
		Date now = new Date();
		if (dateOfBirth != null) {
			long age = now.getTime() - dateOfBirth.getTime();
			return (int) (age / ((long) 1000 * 3600 * 24 * 365));
		} else
			return (0);
	}

	/**
	 * Turns the Date object d into a String using the format given in the
	 * constant DATE_FORMAT.
	 */
	private String dateToString(Date d) {
		if (d != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
			return fmt.format(d);
		} else
			return ("dd-mm-yy");
	}

	/**
	 * Turns the String object s into a Date assuming the format given in the
	 * constant DATE_FORMAT
	 * 
	 * @throws ParseException
	 */
	private Date stringToDate(String s) throws ParseException {
		if (s != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
			return fmt.parse(s);
		} else
			return (null);
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
		this.dateOfBirth = stringToDate(birthday);
	}

	public String getDateOfBirth() {
		return this.dateToString(dateOfBirth);
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
}
