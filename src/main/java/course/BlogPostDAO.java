/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package course;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BlogPostDAO {
    DBCollection postsCollection;

    public BlogPostDAO(final DB blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public DBObject findByPermalink(String permalink) {
        BasicDBObject object = new BasicDBObject("permalink", permalink);
        DBObject post = postsCollection.findOne(object);
        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<DBObject> findByDateDescending(int limit) {
        List<DBObject> posts = postsCollection.find().sort(new BasicDBObject("date", -1)).limit(limit).toArray();
        return posts;
    }


    //add new post
    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        BasicDBObject post = new BasicDBObject();
        post.append("author", username);
        post.append("title", title);
        post.append("body", body);
        post.append("permalink", permalink);
        post.append("tags", tags);
        post.append("comments", new ArrayList<String>());
        post.append("date", new Date());

        postsCollection.insert(post);
        return permalink;
    }

    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        BasicDBObject comment = new BasicDBObject();
        comment.append("author", name);
        comment.append("body", body);

        if (email != null) {
            comment.append("email", email);
        }

        BasicDBObject query = new BasicDBObject("permalink", permalink);

        BasicDBObject item = new BasicDBObject("$push", new BasicDBObject("comments",comment));

        postsCollection.update(query, item);
    }

}
