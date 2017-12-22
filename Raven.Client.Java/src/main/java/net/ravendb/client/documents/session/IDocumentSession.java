package net.ravendb.client.documents.session;

import net.ravendb.client.documents.indexes.AbstractIndexCreationTask;
import net.ravendb.client.documents.queries.Query;
import net.ravendb.client.documents.session.loaders.ILoaderWithInclude;
import net.ravendb.client.primitives.CleanCloseable;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for document session
 */
public interface IDocumentSession extends CleanCloseable {

    /**
     * Get the accessor for advanced operations
     *
     * Those operations are rarely needed, and have been moved to a separate
     * property to avoid cluttering the API
     * @return Advanced session operations
     */
    IAdvancedSessionOperations advanced();

    /**
     * Marks the specified entity for deletion. The entity will be deleted when IDocumentSession.saveChanges is called.
     * @param entity instance of entity to delete
     */
    <T> void delete(T entity);

    /**
     * Marks the specified entity for deletion. The entity will be deleted when DocumentSession.saveChanges is called.
     * WARNING: This method will not call beforeDelete listener!
     * @param id entity id
     */
    void delete(String id);

    /**
     * Marks the specified entity for deletion. The entity will be deleted when DocumentSession.saveChanges is called.
     * WARNING: This method will not call beforeDelete listener!
     * @param id entity Id
     * @param expectedChangeVector Expected change vector of a document to delete.
     */
    void delete(String id, String expectedChangeVector);

    /**
     * Saves all the pending changes to the server.
     */
    void saveChanges();

    /**
     * Stores entity in session with given id and forces concurrency check with given Etag.
     */
    void store(Object entity, String changeVector, String id);

    /**
     * Stores entity in session, extracts Id from entity using Conventions or generates new one if it is not available.
     * Forces concurrency check if the Id is not available during extraction.
     */
    void store(Object entity);

    /**
     * Stores the specified dynamic entity, under the specified id.
     * @param entity entity to store
     * @param id Id to store this entity under. If other entity exists with the same id it will be overwritten.
     */
    void store(Object entity, String id);

    /**
     * Begin a load while including the specified path
     * Path in documents in which server should look for a 'referenced' documents.
     */
    ILoaderWithInclude include(String path);


    //TBD: another includes here?

    /**
     *  Loads the specified entity with the specified id.
     *  @param clazz Object class
     *  @param id Identifier of a entity that will be loaded.
     */
    <T> T load(Class<T> clazz, String id);

    /**
     *  Loads the specified entities with the specified ids.
     */
    <TResult> Map<String, TResult> load(Class<TResult> clazz, String... ids);


    /**
     *  Loads the specified entities with the specified ids.
     */
    <TResult> Map<String, TResult> load(Class<TResult> clazz, Collection<String> ids);

    <T> IDocumentQuery<T> query(Class<T> clazz);

    <T> IDocumentQuery<T> query(Class<T> clazz, Query collectionOrIndexName);

    <T, TIndex extends AbstractIndexCreationTask> IDocumentQuery<T> query(Class<T> clazz, Class<TIndex> indexClazz);

}
