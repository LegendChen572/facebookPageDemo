package tools;

import com.restfb.Connection;
import com.restfb.ConnectionIterator;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Comment;
import com.restfb.types.Conversation;
import com.restfb.types.Group;
import com.restfb.types.MessageTag;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.SendResponse;
import java.util.Iterator;
import java.util.List;

public class FacebookUtil {

    public static final String accessToken = "EAAGWPKGOHdQBAOj2XaZBDdqS1ZBUPWRXJmSVpEFpP2eYtobBZBqkPOG7ZBDEj2opUfAnnPvXZCsHZAZBAgLWVpQYrAgkXnUPpnmiDvFnQQNNi6iZADdH7qvANIu4JnxO8x6HFaXo4HfiZBW85E0bkYwZAZAPiDG1XsaJN8hAr1giBMVr8KN8DnHUyNeUkkUWx3LBuJMALdZB1mtKjHh2PByd8d7cgA8btZCZAzIkc6AdnuqZB0HjAZDZD";
    public static final FacebookClient facebookClient;

    static {
        facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);
    }

    public static String getConversation(String fbName) {
        String var1 = "#r若已滿足條件，請先私訊粉絲團後點選#k";
        IdMessageRecipient var2 = new IdMessageRecipient("{user_id}");
        String var10001 = "301309524125051/conversations";
        Parameter[] var10003 = new Parameter[1];
        boolean var10005 = true;
        var10003[0] = Parameter.with("fields", "id,messages,participants");
        ConnectionIterator var3 = facebookClient.fetchConnection(var10001, Conversation.class, var10003).iterator();

        while (var3.hasNext()) {
            Iterator var4 = ((List) var3.next()).iterator();

            while (var4.hasNext()) {
                Conversation var5 = (Conversation) var4.next();
                boolean var6 = false;
                String var7 = var5.getId();
                Iterator var8 = var5.getParticipants().iterator();

                while (var8.hasNext()) {
                    NamedFacebookType var9 = (NamedFacebookType) var8.next();
                    if (fbName.equals(var9.getName())) {
                        var6 = true;
                        var7 = var5.getId();
                    }
                }

                if (var6) {
                    var10001 = var7 + "/messages";
                    var10003 = new Parameter[2];
                    var10005 = true;
                    var10003[0] = Parameter.with("recipient", var2);
                    var10003[1] = Parameter.with("message", "[分享獎勵] 金鑰 :" + getRandomPassword() + "\r\n請輸入於分拍賣-分享獎勵");
                    SendResponse var10 = (SendResponse) facebookClient.publish(var10001, SendResponse.class, var10003);
                    var1 = "#r金鑰已送達,請至FB私訊查看#k";
                }
            }
        }

        return var1;
    }

    public static void main(String[] a) {
        System.out.println(getSharePageComments("幹"));
    }

    public static String getRandomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int type = (int) (Math.random() * 7.0D % 3.0D);
            switch (type) {
                case 1:
                    sb.append((int) (Math.random() * 10.0D + 48.0D));
                    break;
                case 2:
                    sb.append((char) ((int) (Math.random() * 26 + 65))); // A ~ Z
                    break;
                default:
                    sb.append((char) ((int) (Math.random() * 26 + 97))); // a ~ z
                    break;
            }
        }
        return sb.toString();
    }

    public static String getSharePageComments(String fbName) {
        String var1 = "112406890250439"; // https://www.facebook.com/groups/uunnknownms/?source_id=
        String var2 = "112449750246153"; // 文章編號?
        var1 = var1 + "_" + var2;
        String var10001;
        Parameter[] parameters = new Parameter[1];
        parameters[0] = Parameter.with("fields", "email,first_name,last_name,gender");
        //
        User var10 = (User) facebookClient.fetchObject("me", User.class, parameters);
        parameters = new Parameter[0];
        //
        Connection var3 = facebookClient.fetchConnection("me/friends", User.class, parameters);
        parameters = new Parameter[0];
        //
        Connection var4 = facebookClient.fetchConnection("me/groups", Group.class, parameters);
        System.out.println(var10.getLastName() + var3.getData().size());
        var10001 = var1 + "/comments";
        parameters = new Parameter[1];
        //
        parameters[0] = Parameter.with("fields", "id, permalink_url, from, created_time, message, message_tags, attachment, like_count");
        Connection var8 = facebookClient.fetchConnection(var10001, Comment.class, parameters);
        String retMessage = "#b《留言篩選》#k #d登入FB名字 :#k #r" + fbName + "#k\r\n";
        boolean ret = false;
        ConnectionIterator connectionIter = var8.iterator();

        while (connectionIter.hasNext()) {
            Iterator commentIter = ((List) connectionIter.next()).iterator();

            while (commentIter.hasNext()) {
                Comment comment = (Comment) commentIter.next();
                int tagCount = 0;
                retMessage += "---------------------------------------\r\n";
                retMessage += "#d留言者名字#k : " + comment.getFrom().getName() + "\r\n";
                retMessage += "#d留言時間#k : " + comment.getCreatedTime() + "\r\n";
                retMessage += "#d留言內容#k : " + comment.getMessage() + "\r\n";
                retMessage += "#d標記狀態#k : " + (comment.getMessageTags() == null ? 0 : comment.getMessageTags().size()) + " 件\r\n";
                List<MessageTag> tageMessages = comment.getMessageTags();
                if (tageMessages != null && !tageMessages.isEmpty()) {
                    retMessage += "#d標記人 #k: [";
                    Iterator var14 = tageMessages.iterator();
                    while (var14.hasNext()) {
                        MessageTag tag = (MessageTag) var14.next();
                        if ("user".equals(tag.getType())) {
                            ++tagCount;
                            retMessage += tag.getName() + "/";
                        }
                    }
                    retMessage += "]\r\n";
                }
                retMessage += "#b留言審查#k :#r" + (tagCount >= 3 ? "符合領取標準 " : "不符合領取標準 ") + "#k\r\n";
                if (tagCount >= 3) {
                    ret = true;
                }
            }
        }

        if (ret) {
            return retMessage + "---------------------------------------\r\n#d#L0# 領取分享金鑰#k";
        } else {
            return retMessage + "\r\n#d留言皆不符合領取標準#k";
        }
    }
}
