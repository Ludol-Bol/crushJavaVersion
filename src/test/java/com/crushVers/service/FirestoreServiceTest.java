package com.crushVers.service;

import com.crushVers.model.User;
import com.crushVers.model.UserToken;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
// TODO: Добавить проверку на пустую строку перед сохранением
@ExtendWith(MockitoExtension.class)
class FirestoreServiceTest {

    private FirestoreService firestoreService;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference usersCollection;

    @Mock
    private CollectionReference tokensCollection;

    @Mock
    private DocumentReference userDocument;

    @Mock
    private DocumentReference tokenDocument;

    @Mock
    private ApiFuture<QuerySnapshot> queryFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentFuture;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private QuerySnapshot querySnapshot;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;

    @Mock
    private Query query;

    private MockedStatic<FirestoreClient> firestoreClientMock;

    @BeforeEach
    void setUp() {
        firestoreService = new FirestoreService();

        if (firestoreClientMock != null) {
            firestoreClientMock.close();
        }

        firestoreClientMock = mockStatic(FirestoreClient.class);
        firestoreClientMock.when(FirestoreClient::getFirestore).thenReturn(firestore);
    }

    @AfterEach
    void tearDown() {
        if (firestoreClientMock != null) {
            firestoreClientMock.close();
        }
    }

    //findByEmail

    @Test
    void testFindByEmail_userExists() throws ExecutionException, InterruptedException {
        String email = "test@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        expectedUser.setNickname("testuser");

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.whereEqualTo("email", email)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(expectedUser);
        when(queryDocumentSnapshot.getId()).thenReturn("doc123");

        User result = firestoreService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("testuser", result.getNickname());
        assertEquals("doc123", result.getId());
    }

    @Test
    void testFindByEmail_userNotFound() throws ExecutionException, InterruptedException {
        String email = "notfound@example.com";

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.whereEqualTo("email", email)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        User result = firestoreService.findByEmail(email);

        assertNull(result);
    }

    //findByNickname

    @Test
    void testFindByNickname_userExists() throws ExecutionException, InterruptedException {
        String nickname = "testuser";
        User expectedUser = new User();
        expectedUser.setEmail("test@example.com");
        expectedUser.setNickname(nickname);

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.whereEqualTo("nickname", nickname)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(expectedUser);
        when(queryDocumentSnapshot.getId()).thenReturn("doc123");

        User result = firestoreService.findByNickname(nickname);

        assertNotNull(result);
        assertEquals(nickname, result.getNickname());
    }

    @Test
    void testFindByNickname_userNotFound() throws ExecutionException, InterruptedException {
        String nickname = "notfound";

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.whereEqualTo("nickname", nickname)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        User result = firestoreService.findByNickname(nickname);

        assertNull(result);
    }

    //findByEmailOrNickname

    @Test
    void testFindByEmailOrNickname_byEmail() throws ExecutionException, InterruptedException {
        String login = "test@example.com";
        User expectedUser = new User();
        expectedUser.setEmail(login);

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.whereEqualTo("email", login)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(expectedUser);
        when(queryDocumentSnapshot.getId()).thenReturn("doc123");

        User result = firestoreService.findByEmailOrNickname(login);

        assertNotNull(result);
        assertEquals(login, result.getEmail());
    }

    @Test
    void testFindByEmailOrNickname_byNickname() throws ExecutionException, InterruptedException {
        String login = "testuser";
        User expectedUser = new User();
        expectedUser.setNickname(login);

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.whereEqualTo("email", login)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        when(usersCollection.whereEqualTo("nickname", login)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(User.class)).thenReturn(expectedUser);
        when(queryDocumentSnapshot.getId()).thenReturn("doc123");

        User result = firestoreService.findByEmailOrNickname(login);

        assertNotNull(result);
        assertEquals(login, result.getNickname());
    }

    //findById

    @Test
    void testFindById_userExists() throws ExecutionException, InterruptedException {
        String userId = "user123";
        User expectedUser = new User();
        expectedUser.setEmail("test@example.com");

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.document(userId)).thenReturn(userDocument);
        when(userDocument.get()).thenReturn(documentFuture);
        when(documentFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(User.class)).thenReturn(expectedUser);
        when(documentSnapshot.getId()).thenReturn(userId);

        User result = firestoreService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void testFindById_userNotFound() throws ExecutionException, InterruptedException {
        String userId = "notfound";

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.document(userId)).thenReturn(userDocument);
        when(userDocument.get()).thenReturn(documentFuture);
        when(documentFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);

        User result = firestoreService.findById(userId);

        assertNull(result);
    }

    //hashPassword

    @Test
    void testHashPassword() {
        String password = "password123";
        String hash1 = firestoreService.hashPassword(password);
        String hash2 = firestoreService.hashPassword(password);

        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashPassword_differentPasswords() {
        String hash1 = firestoreService.hashPassword("password123");
        String hash2 = firestoreService.hashPassword("different");

        assertNotEquals(hash1, hash2);
    }

    @Test
    void testHashPassword_emptyString() {
        String hash = firestoreService.hashPassword("");
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    //checkPassword

    @Test
    void testCheckPassword_correct() {
        String password = "password123";
        String hash = firestoreService.hashPassword(password);

        boolean result = firestoreService.checkPassword(password, hash);

        assertTrue(result);
    }

    @Test
    void testCheckPassword_incorrect() {
        String password = "password123";
        String hash = firestoreService.hashPassword(password);
        String wrongPassword = "wrongpassword";

        boolean result = firestoreService.checkPassword(wrongPassword, hash);

        assertFalse(result);
    }

    //saveUser

    @Test
    void testSaveUser_newUser() throws ExecutionException, InterruptedException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setNickname("testuser");

        when(firestore.collection("users")).thenReturn(usersCollection);
        // Используем anyString() для любого ID
        when(usersCollection.document(anyString())).thenReturn(userDocument);
        when(userDocument.set(user)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        User result = firestoreService.saveUser(user);

        assertNotNull(result);
        assertNotNull(result.getId());
        verify(userDocument, times(1)).set(user);
    }

    @Test
    void testSaveUser_existingUser() throws ExecutionException, InterruptedException {
        User user = new User();
        user.setId("existing-id");
        user.setEmail("test@example.com");

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.document("existing-id")).thenReturn(userDocument);
        when(userDocument.set(user)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        User result = firestoreService.saveUser(user);

        assertNotNull(result);
        assertEquals("existing-id", result.getId());
        verify(userDocument, times(1)).set(user);
    }

    //findAllUsers

    @Test
    void testFindAllUsers() throws ExecutionException, InterruptedException {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");

        QueryDocumentSnapshot mockDoc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot mockDoc2 = mock(QueryDocumentSnapshot.class);

        when(mockDoc1.toObject(User.class)).thenReturn(user1);
        when(mockDoc1.getId()).thenReturn("doc1");
        when(mockDoc2.toObject(User.class)).thenReturn(user2);
        when(mockDoc2.getId()).thenReturn("doc2");

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc1, mockDoc2));

        List<User> result = firestoreService.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindAllUsers_empty() throws ExecutionException, InterruptedException {
        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(List.of());

        List<User> result = firestoreService.findAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //user_tokens

    @Test
    void testSaveUserToken() throws ExecutionException, InterruptedException {
        UserToken token = new UserToken();
        token.setUserId("user123");
        token.setToken("token123");

        when(firestore.collection("user_tokens")).thenReturn(tokensCollection);
        when(tokensCollection.document(anyString())).thenReturn(tokenDocument);
        when(tokenDocument.set(token)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        firestoreService.saveUserToken(token);

        verify(tokenDocument, times(1)).set(token);
    }

    @Test
    void testFindTokenByValue_exists() throws ExecutionException, InterruptedException {
        String tokenValue = "token123";
        UserToken expectedToken = new UserToken();
        expectedToken.setToken(tokenValue);
        expectedToken.setUserId("user123");

        when(firestore.collection("user_tokens")).thenReturn(tokensCollection);
        when(tokensCollection.whereEqualTo("token", tokenValue)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(UserToken.class)).thenReturn(expectedToken);

        UserToken result = firestoreService.findTokenByValue(tokenValue);

        assertNotNull(result);
        assertEquals(tokenValue, result.getToken());
    }

    @Test
    void testFindTokenByValue_notExists() throws ExecutionException, InterruptedException {
        String tokenValue = "notfound";

        when(firestore.collection("user_tokens")).thenReturn(tokensCollection);
        when(tokensCollection.whereEqualTo("token", tokenValue)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        UserToken result = firestoreService.findTokenByValue(tokenValue);

        assertNull(result);
    }

    @Test
    void testDeleteUserToken() throws ExecutionException, InterruptedException {
        String tokenValue = "token123";
        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        DocumentReference mockRef = mock(DocumentReference.class);

        when(firestore.collection("user_tokens")).thenReturn(tokensCollection);
        when(tokensCollection.whereEqualTo("token", tokenValue)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(List.of(mockDoc));
        when(mockDoc.getReference()).thenReturn(mockRef);
        when(mockRef.delete()).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(null);

        firestoreService.deleteUserToken(tokenValue);

        verify(mockRef, times(1)).delete();
    }

    //findUserByToken

    @Test
    void testFindUserByToken_validToken() throws ExecutionException, InterruptedException {
        String tokenValue = "validToken";
        String userId = "user123";
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setEmail("test@example.com");

        UserToken userToken = new UserToken();
        userToken.setUserId(userId);

        when(firestore.collection("user_tokens")).thenReturn(tokensCollection);
        when(tokensCollection.whereEqualTo("token", tokenValue)).thenReturn(query);
        // Используем any() для Date
        when(query.whereGreaterThan(eq("expiresAt"), any(Date.class))).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(queryDocumentSnapshot));
        when(queryDocumentSnapshot.toObject(UserToken.class)).thenReturn(userToken);

        when(firestore.collection("users")).thenReturn(usersCollection);
        when(usersCollection.document(userId)).thenReturn(userDocument);
        when(userDocument.get()).thenReturn(documentFuture);
        when(documentFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(User.class)).thenReturn(expectedUser);
        when(documentSnapshot.getId()).thenReturn(userId);

        User result = firestoreService.findUserByToken(tokenValue);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void testFindUserByToken_expiredToken() throws ExecutionException, InterruptedException {
        String tokenValue = "expiredToken";

        when(firestore.collection("user_tokens")).thenReturn(tokensCollection);
        when(tokensCollection.whereEqualTo("token", tokenValue)).thenReturn(query);
        when(query.whereGreaterThan(eq("expiresAt"), any(Date.class))).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        User result = firestoreService.findUserByToken(tokenValue);

        assertNull(result);
    }
}