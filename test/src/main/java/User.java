import java.util.ArrayList;
import java.util.List;

import org.jcouchdb.document.BaseDocument;

public class User extends BaseDocument {
	private String firstname;
	private String lastname;
	private List<String> sprachen = new ArrayList<String>();

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public List<String> getSprachen() {
		return sprachen;
	}

	public void setSprachen(List<String> sprachen) {
		this.sprachen = sprachen;
	}

	@Override
	public String toString() {
		return "User [firstname=" + firstname + ", lastname=" + lastname + ", sprachen=" + sprachen + "]";
	}
}
