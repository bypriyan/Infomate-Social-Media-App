package com.bypriyan.infomate.Model;

public class ModelComment {

    String commentId, comment, timestamp, uid, email, image, name;

    public ModelComment() {
    }

    public ModelComment(String commentId, String comment, String timestamp, String uid, String email, String image, String name) {
        this.commentId = commentId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.email = email;
        this.image = image;
        this.name = name;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
