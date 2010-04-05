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

import com.mongodb.*;

public class TestMongo {

    public static void main(String[] args) throws Exception {
        // Mongo m = new Mongo();
        // m.dropDatabase("testen");
        // DB db = m.getDB("testen");
        // DBCollection coll = db.getCollection("testCollection");
        // BasicDBObjectBuilder start = BasicDBObjectBuilder.start("Hallo",
        // "Welt");
        // DBObject dbObject = start.get();
        // // dbObject.markAsPartialObject();
        // coll.save(dbObject);
        //
        // dbObject.markAsPartialObject();
        // start = BasicDBObjectBuilder.start("Jooo",
        // "Mann").add("subirgendwas", dbObject);
        // coll.save(start.get());

        Mongo m = new Mongo();
        // m.dropDatabase("testen");
        DB db = m.getDB("testen");
        DBCollection coll = db.getCollection("testCollection");
        coll.setObjectClass(User.class);
        System.out.println(coll.getCount());
        BasicDBObjectBuilder start = BasicDBObjectBuilder.start();
        start.append("Firstname", "Carsten");
        // coll.createIndex(start.get());
        DBCursor find = coll.find(start.get());
        System.out.println(find.count());
        long currentTimeMillis = System.currentTimeMillis();
        find = coll.find(start.get());
        User next = (User) find.next();
        // next.setFirstname("Jomann");
        // coll.save(next);
        System.out.println(next);
        System.out.println(System.currentTimeMillis() - currentTimeMillis);

        // GridFS file = new GridFS(db);
        // GridFSInputFile createFile = file.createFile(new
        // File("D:/Mirror E/Video/Reportagen/Bob Ross - High Tide.avi"));
        // createFile.save();
        // GridFS file = new GridFS(db, "testen");
        // List<GridFSDBFile> find = file.find("Bob Ross - High Tide.avi");
        // System.out.println(find);
        // GridFSDBFile gridFSDBFile = find.get(0);
        // gridFSDBFile.writeTo("d:/test.avi");
        // Role role = new Role();
        // role.setRoledesc("jerole");
        // role.setRolename("jename");
        // coll.insert(role);
        // role.markAsPartialObject();
        // User user = new User();
        // user.set_id("1234567890");
        // user.setFirstname("Carsten");
        // user.setLastname("Hufe");
        // user.getSprachen().add("java");
        // user.getSprachen().add("sql");
        // // user.getRole().setRoledesc("roledescription");
        // // user.getRole().setRolename("rolenamejo");
        // role.markAsPartialObject();
        // user.setRole(role);
        // coll.insert(user);
        // List<DBObject> users = new ArrayList<DBObject>();
        // for (int i = 0; i < 1000000; i++) {
        // User user = new User();
        // // user.setBirthdate(new Date());
        // user.setFirstname("Carsten" + i);
        // user.setLastname("Hufe" + i);
        // user.getSprachen().add("java");
        // user.getSprachen().add("sql");
        // user.getRole().setRoledesc("roledescription" + i);
        // user.getRole().setRolename("rolenamejo" + i);
        // coll.insert(user);
        // }
        // User user = new User();
        // // user.setBirthdate(new Date());
        // user.setFirstname("Carsten");
        // user.setLastname("Hufe");
        // user.getSprachen().add("java");
        // user.getSprachen().add("sql");
        // user.getRole().setRoledesc("roledescription");
        // user.getRole().setRolename("rolenamejo");
        // long currentTimeMillis = System.currentTimeMillis();
        // coll.insert(user);
        // System.out.println(System.currentTimeMillis() - currentTimeMillis);
        // currentTimeMillis = System.currentTimeMillis();
        // coll.insert(users);
        // System.out.println(System.currentTimeMillis() - currentTimeMillis);

    }
}
