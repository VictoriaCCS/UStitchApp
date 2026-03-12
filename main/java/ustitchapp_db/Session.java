/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ustitchapp_db;

/**
 *
 * @author vcaro
 */

public class Session {

    private static int currentUserId = -1;
    private static String currentUserEmail = null;

    public static void setCurrentUserId(int id) {
        currentUserId = id;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserEmail(String email) {
        currentUserEmail = email;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public static void clearSession() {
        currentUserId = -1;
        currentUserEmail = null;
    }
}
