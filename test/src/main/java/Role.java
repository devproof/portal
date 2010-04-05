/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mongodb.ReflectionDBObject;

import java.lang.Object;
import java.lang.Override;
import java.util.HashSet;
import java.util.Set;

public class Role extends ReflectionDBObject {
    private String rolename;
    private String roledesc;

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getRoledesc() {
        return roledesc;
    }

    public void setRoledesc(String roledesc) {
        this.roledesc = roledesc;
    }

    @Override
    public String toString() {
        return "Role [roledesc=" + roledesc + ", rolename=" + rolename + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        if (!super.equals(o)) return false;

        Role role = (Role) o;

        if (partial != role.partial) return false;
        if (roledesc != null ? !roledesc.equals(role.roledesc) : role.roledesc != null) return false;
        if (rolename != null ? !rolename.equals(role.rolename) : role.rolename != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (rolename != null ? rolename.hashCode() : 0);
        result = 31 * result + (roledesc != null ? roledesc.hashCode() : 0);
        result = 31 * result + (partial ? 1 : 0);
        return result;
    }

    boolean partial = false;

    @Override
    public boolean isPartialObject() {
        return partial;
    }

    @Override
    public void markAsPartialObject() {
        partial = true;
    }

    @Override
    public Set<String> keySet() {
        if (partial) {
            Set<String> set = new HashSet<String>();
            set.add("_id");
            return set;
        }
        return super.keySet();
    }

}
