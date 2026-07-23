package com.crushVers.service;

import com.crushVers.model.UserRole;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserRoleServiceTest {

    private UserRoleService userRoleService;
    private Firestore firestore;
    private MockedStatic<FirestoreClient> firestoreClientMock;

    @BeforeEach
    void setUp() {
        userRoleService = new UserRoleService();
        firestore = mock(Firestore.class);
        firestoreClientMock = mockStatic(FirestoreClient.class);
        firestoreClientMock.when(FirestoreClient::getFirestore).thenReturn(firestore);
    }

    @AfterEach
    void tearDown() {
        firestoreClientMock.close();
    }

    // ===== ТЕСТЫ ДЛЯ findByName =====

    @Test
    void testFindByName_roleExists() throws ExecutionException, InterruptedException {
        String roleName = "ROLE_USER";
        UserRole expectedRole = new UserRole();
        expectedRole.setName(roleName);
        expectedRole.setDescription("Обычный пользователь");

        CollectionReference rolesCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", roleName)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.isEmpty()).thenReturn(false);
        when(snapshot.getDocuments()).thenReturn(List.of(doc));
        when(doc.toObject(UserRole.class)).thenReturn(expectedRole);

        UserRole result = userRoleService.findByName(roleName);

        assertNotNull(result);
        assertEquals(roleName, result.getName());
        assertEquals("Обычный пользователь", result.getDescription());
    }

    @Test
    void testFindByName_roleNotFound() throws ExecutionException, InterruptedException {
        String roleName = "ROLE_NOT_EXISTS";

        CollectionReference rolesCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", roleName)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.isEmpty()).thenReturn(true);

        UserRole result = userRoleService.findByName(roleName);

        assertNull(result);
    }

    // ===== ТЕСТЫ ДЛЯ findById =====

    @Test
    void testFindById_roleExists() throws ExecutionException, InterruptedException {
        String roleId = "role123";
        UserRole expectedRole = new UserRole();
        expectedRole.setId(roleId);
        expectedRole.setName("ROLE_ADMIN");

        CollectionReference rolesCollection = mock(CollectionReference.class);
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.document(roleId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.toObject(UserRole.class)).thenReturn(expectedRole);

        UserRole result = userRoleService.findById(roleId);

        assertNotNull(result);
        assertEquals(roleId, result.getId());
        assertEquals("ROLE_ADMIN", result.getName());
    }

    @Test
    void testFindById_roleNotFound() throws ExecutionException, InterruptedException {
        String roleId = "notfound";

        CollectionReference rolesCollection = mock(CollectionReference.class);
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<DocumentSnapshot> future = mock(ApiFuture.class);
        DocumentSnapshot snapshot = mock(DocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.document(roleId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.exists()).thenReturn(false);

        UserRole result = userRoleService.findById(roleId);

        assertNull(result);
    }

    // ===== ТЕСТЫ ДЛЯ findAll =====

    @Test
    void testFindAll_returnsList() throws ExecutionException, InterruptedException {
        UserRole role1 = new UserRole();
        role1.setName("ROLE_USER");
        UserRole role2 = new UserRole();
        role2.setName("ROLE_ADMIN");

        CollectionReference rolesCollection = mock(CollectionReference.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.getDocuments()).thenReturn(List.of(doc1, doc2));
        when(doc1.toObject(UserRole.class)).thenReturn(role1);
        when(doc2.toObject(UserRole.class)).thenReturn(role2);

        List<UserRole> result = userRoleService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ROLE_USER", result.get(0).getName());
        assertEquals("ROLE_ADMIN", result.get(1).getName());
    }

    @Test
    void testFindAll_emptyList() throws ExecutionException, InterruptedException {
        CollectionReference rolesCollection = mock(CollectionReference.class);
        ApiFuture future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        // ✅ ВАЖНО: возвращаем пустой список, а не null
        when(snapshot.getDocuments()).thenReturn(List.of());

        List<UserRole> result;
        result = userRoleService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===== ТЕСТЫ ДЛЯ save =====

    @Test
    void testSave_newRole() throws ExecutionException, InterruptedException {
        UserRole newRole = new UserRole();
        newRole.setName("ROLE_MODERATOR");
        newRole.setDescription("Модератор");

        CollectionReference rolesCollection = mock(CollectionReference.class);
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<WriteResult> future = mock(ApiFuture.class);
        ApiFuture<QuerySnapshot> queryFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);

        Query query = mock(Query.class);
        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", "ROLE_MODERATOR")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(true);

        when(rolesCollection.document()).thenReturn(docRef);
        when(docRef.set(newRole)).thenReturn(future);
        when(future.get()).thenReturn(null);

        UserRole result = userRoleService.save(newRole);

        assertNotNull(result);
        assertEquals("ROLE_MODERATOR", result.getName());
        assertNotNull(result.getCreatedAt());
        verify(docRef, times(1)).set(newRole);
    }

    @Test
    void testSave_existingRole() throws ExecutionException, InterruptedException {
        UserRole existingRole = new UserRole();
        existingRole.setName("ROLE_USER");
        existingRole.setDescription("Обычный пользователь");
        existingRole.setCreatedAt(new Date());

        UserRole updatedRole = new UserRole();
        updatedRole.setName("ROLE_USER");
        updatedRole.setDescription("Обычный пользователь (обновлено)");

        CollectionReference rolesCollection = mock(CollectionReference.class);
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<WriteResult> future = mock(ApiFuture.class);

        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> queryFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", "ROLE_USER")).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(doc));
        when(doc.getId()).thenReturn("existingDocId");

        when(rolesCollection.document("existingDocId")).thenReturn(docRef);
        when(docRef.set(updatedRole)).thenReturn(future);
        when(future.get()).thenReturn(null);

        UserRole result = userRoleService.save(updatedRole);

        assertNotNull(result);
        assertEquals("ROLE_USER", result.getName());
        assertEquals("Обычный пользователь (обновлено)", result.getDescription());
        verify(docRef, times(1)).set(updatedRole);
    }

    @Test
    void testSave_roleNameNull() {
        UserRole role = new UserRole();
        role.setDescription("Без имени");

        assertThrows(IllegalArgumentException.class, () -> {
            userRoleService.save(role);
        });
    }

    // ===== ТЕСТЫ ДЛЯ getIdByName =====

    @Test
    void testGetIdByName_exists() throws ExecutionException, InterruptedException {
        String roleName = "ROLE_USER";
        String expectedId = "doc123";

        CollectionReference rolesCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", roleName)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.isEmpty()).thenReturn(false);
        when(snapshot.getDocuments()).thenReturn(List.of(doc));
        when(doc.getId()).thenReturn(expectedId);

        String result = userRoleService.getIdByName(roleName);

        assertEquals(expectedId, result);
    }

    @Test
    void testGetIdByName_notExists() throws ExecutionException, InterruptedException {
        String roleName = "ROLE_NOT_EXISTS";

        CollectionReference rolesCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", roleName)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.isEmpty()).thenReturn(true);

        String result = userRoleService.getIdByName(roleName);

        assertNull(result);
    }

    // ===== ТЕСТЫ ДЛЯ deleteByName =====

    @Test
    void testDeleteByName_exists() throws ExecutionException, InterruptedException {
        String roleName = "ROLE_USER";
        String docId = "doc123";

        CollectionReference rolesCollection = mock(CollectionReference.class);
        DocumentReference docRef = mock(DocumentReference.class);
        ApiFuture<WriteResult> future = mock(ApiFuture.class);

        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> queryFuture = mock(ApiFuture.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot doc = mock(QueryDocumentSnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", roleName)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(queryFuture);
        when(queryFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.isEmpty()).thenReturn(false);
        when(querySnapshot.getDocuments()).thenReturn(List.of(doc));
        when(doc.getId()).thenReturn(docId);

        when(rolesCollection.document(docId)).thenReturn(docRef);
        when(docRef.delete()).thenReturn(future);
        when(future.get()).thenReturn(null);

        userRoleService.deleteByName(roleName);

        verify(docRef, times(1)).delete();
    }

    @Test
    void testDeleteByName_notExists() throws ExecutionException, InterruptedException {
        String roleName = "ROLE_NOT_EXISTS";

        CollectionReference rolesCollection = mock(CollectionReference.class);
        Query query = mock(Query.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.whereEqualTo("name", roleName)).thenReturn(query);
        when(query.limit(1)).thenReturn(query);
        when(query.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.isEmpty()).thenReturn(true);

        userRoleService.deleteByName(roleName);
        verify(firestore, never()).collection(anyString());
    }

    // ===== ТЕСТЫ ДЛЯ count =====

    @Test
    void testCount_returnsCorrectNumber() throws ExecutionException, InterruptedException {
        long expectedCount = 5;

        CollectionReference rolesCollection = mock(CollectionReference.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.size()).thenReturn((int) expectedCount);

        long result = userRoleService.count();

        assertEquals(expectedCount, result);
    }

    @Test
    void testCount_whenEmpty() throws ExecutionException, InterruptedException {
        CollectionReference rolesCollection = mock(CollectionReference.class);
        ApiFuture<QuerySnapshot> future = mock(ApiFuture.class);
        QuerySnapshot snapshot = mock(QuerySnapshot.class);

        when(firestore.collection("user_roles")).thenReturn(rolesCollection);
        when(rolesCollection.get()).thenReturn(future);
        when(future.get()).thenReturn(snapshot);
        when(snapshot.size()).thenReturn(0);

        long result = userRoleService.count();

        assertEquals(0, result);
    }
}