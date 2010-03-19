import java.util.ArrayList;
import java.util.List;

import org.jcouchdb.db.Database;

public class TestPersist {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database db = new Database("localhost", "testen3");
		// ViewResult<User> queryView = db.queryView("aaaa/aaaa", User.class,
		// null, null);
		// List<ValueRow<User>> rows = queryView.getRows();
		// for (ValueRow<User> row : rows) {
		// System.out.println(row.getValue());
		// }

		List<User> users = new ArrayList<User>();
		for (int i = 0; i < 10000; i++) {
			User user = new User();
			// user.setBirthdate(new Date());
			user.setFirstname("Carsten" + i);
			user.setLastname("Hufe" + i);
			user.getSprachen().add("java");
			user.getSprachen().add("sql");
			users.add(user);
			// create the document in couchdb
			// long currentTimeMillis = System.currentTimeMillis();
			// db.createDocument(user);
			// System.out.println(System.currentTimeMillis() -
			// currentTimeMillis);
		}
		User user = new User();
		// user.setBirthdate(new Date());
		user.setFirstname("Carsten");
		user.setLastname("Hufe");
		user.getSprachen().add("java");
		user.getSprachen().add("sql");
		long currentTimeMillis = System.currentTimeMillis();
		db.createDocument(user);
		System.out.println(System.currentTimeMillis() - currentTimeMillis);
		currentTimeMillis = System.currentTimeMillis();
		db.bulkCreateDocuments(users);
		System.out.println(System.currentTimeMillis() - currentTimeMillis);
	}
}
