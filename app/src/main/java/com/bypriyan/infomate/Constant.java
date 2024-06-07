package com.bypriyan.infomate;

import java.util.HashMap;

public class Constant {

    public static final String KEY_USERS = "users";
    public static final String KEY_UID = "uid";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "mobileNumber";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "InfoMate";
    public static final String KEY_CATEGORY = "branch";
    public static final String KEY_CHATS = "chats";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_RECIVER= "reciver";
    public static final String KEY_MESSAGE= "message";
    public static final String KEY_TIMESTAMP= "timestamp";
    public static final String KEY_SEEN= "isSeen";
    public static final String KEY_ONLINE_STAUS= "onlineStatus";
    public static final String KEY_TYPING_STAUS= "typingStatus";

    public static final String KEY_CHATLIST = "ChatList";
    public static final String KEY_BLOCKEDUSERS = "BlockedUser";

    public static final String KEY_POST_ID = "postId";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_POST_TITLE = "postTitle";
    public static final String KEY_POST_DESCRIPTION = "postDescription";
    public static final String KEY_POST_IMAGE = "postImage";
    public static final String KEY_POST_TIME = "postTime";
    public static final String KEY_NO_IMAGE = "noImage";
    public static final String KEY_POST_LIKES = "postLikes";

    public static final String KEY_COMMENT= "Comment";
    public static final String KEY_COMMENT_ID= "commentId";
    public static final String KEY_USERS_COMMENT= "comment";

    public static final String KEY_GROUP_ID= "groupId";
    public static final String KEY_GROUP_DESCRIPTION= "groupDescription";
    public static final String KEY_GROUP_TITLE= "grooupTitle";
    public static final String KEY_GROUP_ICON= "grooupIcon";
    public static final String KEY_GROUP_TIMESTAMP= "grooupTimeStamp";
    public static final String KEY_CREATED_BY= "createdBy";
    public static final String KEY_GROUPS= "groups";
    public static final String KEY_ROLE= "role";
    public static final String KEY_PARTICIPENTS= "participants";


    public static final String KEY_POST_COMMENTS= "postComments";

    public static final String KEY_LIKES = "Likes";

    public static final String KEY_TOKENS = "Tokens";


    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";


    public static HashMap<String, String> remoteMsgHeader = null;
    public static HashMap<String, String> getRemoteMsgHeader(){
        if(remoteMsgHeader == null){
            remoteMsgHeader = new HashMap<>();
            remoteMsgHeader.put(
                    REMOTE_MSG_AUTHORIZATION,

                    "key=AAAAWrR2uJQ:APA91bEsisf2-ksjcUyIFXpNvwQfuFThUv34V-f2k1yPsQ2HWkUqb9mvtvvMozsrw0n-WLVtx4CN59NGpRMLwgG0ZQK2J3i_vXZ_QMNxeqeccqvgI2Rf5jmy7ULYsk9OFmXSbCyq3epf"
            );
            remoteMsgHeader.put(REMOTE_MSG_CONTENT_TYPE,
                    "application/json");
        }
        return remoteMsgHeader;
    }



}
