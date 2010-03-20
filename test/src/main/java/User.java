import java.util.ArrayList;
import java.util.List;

import com.mongodb.ReflectionDBObject;

public class User extends ReflectionDBObject {
	private String firstname;
	private String lastname;
	private List<String> sprachen = new ArrayList<String>();
	private Role role = new Role();

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

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
		return "User [firstname=" + firstname + ", lastname=" + lastname + ", role=" + role + ", sprachen=" + sprachen
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((sprachen == null) ? 0 : sprachen.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (sprachen == null) {
			if (other.sprachen != null)
				return false;
		} else if (!sprachen.equals(other.sprachen))
			return false;
		return true;
	}
}
