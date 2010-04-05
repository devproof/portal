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

import org.jcouchdb.db.Database;

import java.util.ArrayList;
import java.util.List;

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
        // ViewResult<Map> listDocuments = db.listDocuments(null, null);

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
        // db.bulkCreateDocuments(users);
        System.out.println(System.currentTimeMillis() - currentTimeMillis);
    }
}
