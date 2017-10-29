package net.ravendb.client.documents.session;

/**
 *  A query against a Raven index
 */
public interface IDocumentQueryBase<T, TSelf extends IDocumentQueryBase<T, TSelf>> extends IQueryBase<T, TSelf> {
    /* TODO
     /// <summary>
        ///  The last term that we asked the query to use equals on
        /// </summary>
        /// <param name="isAsync"></param>
        KeyValuePair<string, object> GetLastEqualityTerm(bool isAsync = false);
        /// <summary>
        ///     Negate the next operation
        /// </summary>
        TSelf Not { get; }

        /// <summary>
        ///     Adds an ordering for a specific field to the query
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="descending">if set to <c>true</c> [descending].</param>
        /// <param name="ordering">ordering type.</param>
        TSelf AddOrder(string fieldName, bool descending = false, OrderingType ordering = OrderingType.String);

        /// <summary>
        ///     Adds an ordering for a specific field to the query
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="descending">if set to <c>true</c> [descending].</param>
        /// <param name="ordering">Ordering type.</param>
        TSelf AddOrder<TValue>(Expression<Func<T, TValue>> propertySelector, bool descending = false, OrderingType ordering = OrderingType.String);

        /// <summary>
        ///     Add an AND to the query
        /// </summary>
        TSelf AndAlso();

        /// <summary>
        ///     Specifies a boost weight to the last where clause.
        ///     The higher the boost factor, the more relevant the term will be.
        /// </summary>
        /// <param name="boost">
        ///     boosting factor where 1.0 is default, less than 1.0 is lower weight, greater than 1.0 is higher
        ///     weight
        /// </param>
        /// <returns></returns>
        /// <remarks>
        ///     http://lucene.apache.org/java/2_4_0/queryparsersyntax.html#Boosting%20a%20Term
        /// </remarks>
        TSelf Boost(decimal boost);

        /// <summary>
        ///     Simplified method for closing a clause within the query
        /// </summary>
        /// <returns></returns>
        TSelf CloseSubclause();

        /// <summary>
        ///     Performs a query matching ALL of the provided values against the given field (AND)
        /// </summary>
        TSelf ContainsAll(string fieldName, IEnumerable<object> values);

        /// <summary>
        ///     Performs a query matching ALL of the provided values against the given field (AND)
        /// </summary>
        TSelf ContainsAll<TValue>(Expression<Func<T, TValue>> propertySelector, IEnumerable<TValue> values);

        /// <summary>
        ///     Performs a query matching ANY of the provided values against the given field (OR)
        /// </summary>
        TSelf ContainsAny(string fieldName, IEnumerable<object> values);

        /// <summary>
        ///     Performs a query matching ANY of the provided values against the given field (OR)
        /// </summary>
        TSelf ContainsAny<TValue>(Expression<Func<T, TValue>> propertySelector, IEnumerable<TValue> values);

        /// <summary>
        ///     This function exists solely to forbid in memory where clause on IDocumentQuery, because
        ///     that is nearly always a mistake.
        /// </summary>
        [Obsolete(@"
You cannot issue an in memory filter - such as Count(x=>x.Name == ""Ayende"") - on IDocumentQuery.
This is likely a bug, because this will execute the filter in memory, rather than in RavenDB.
Consider using session.Query<T>() instead of session.DocumentQuery<T>. The session.Query<T>() method fully supports Linq queries, while session.DocumentQuery<T>() is intended for lower level API access.
If you really want to do in memory filtering on the data returned from the query, you can use: session.DocumentQuery<T>().ToList().Count(x=>x.Name == ""Ayende"")
", true)]
        int Count(Func<T, bool> predicate);

        /// <summary>
        ///     Apply distinct operation to this query
        /// </summary>
        TSelf Distinct();

        /// <summary>
        ///     Adds explanations of scores calculated for queried documents to the query result
        /// </summary>
        TSelf ExplainScores();

        /// <summary>
        ///     Specifies a fuzziness factor to the single word term in the last where clause
        /// </summary>
        /// <param name="fuzzy">0.0 to 1.0 where 1.0 means closer match</param>
        /// <returns></returns>
        /// <remarks>
        ///     http://lucene.apache.org/java/2_4_0/queryparsersyntax.html#Fuzzy%20Searches
        /// </remarks>
        TSelf Fuzzy(decimal fuzzy);

        /// <summary>
        ///     Adds matches highlighting for the specified field.
        /// </summary>
        /// <remarks>
        ///     The specified field should be analyzed and stored for highlighter to work.
        ///     For each match it creates a fragment that contains matched text surrounded by highlighter tags.
        /// </remarks>
        /// <param name="fieldName">The field name to highlight.</param>
        /// <param name="fragmentLength">The fragment length.</param>
        /// <param name="fragmentCount">The maximum number of fragments for the field.</param>
        /// <param name="fragmentsField">The field in query results item to put highlights into.</param>
        TSelf Highlight(string fieldName, int fragmentLength, int fragmentCount, string fragmentsField);

        /// <summary>
        ///     Adds matches highlighting for the specified field.
        /// </summary>
        /// <remarks>
        ///     The specified field should be analyzed and stored for highlighter to work.
        ///     For each match it creates a fragment that contains matched text surrounded by highlighter tags.
        /// </remarks>
        /// <param name="fieldName">The field name to highlight.</param>
        /// <param name="fragmentLength">The fragment length.</param>
        /// <param name="fragmentCount">The maximum number of fragments for the field.</param>
        /// <param name="highlightings">Field highlights for all results.</param>
        TSelf Highlight(string fieldName, int fragmentLength, int fragmentCount, out FieldHighlightings highlightings);

        /// <summary>
        ///     Adds matches highlighting for the specified field on a Map/Reduce Index.
        /// </summary>
        /// <remarks>
        ///     This is only valid for Map/Reduce Index queries.
        ///     The specified field and key should be analyzed and stored for highlighter to work.
        ///     For each match it creates a fragment that contains matched text surrounded by highlighter tags.
        /// </remarks>
        /// <param name="fieldName">The field name to highlight.</param>
        /// <param name="fieldKeyName">The field key name to associate highlights with.</param>
        /// <param name="fragmentLength">The fragment length.</param>
        /// <param name="fragmentCount">The maximum number of fragments for the field.</param>
        /// <param name="highlightings">Field highlights for all results.</param>
        TSelf Highlight(string fieldName, string fieldKeyName, int fragmentLength, int fragmentCount, out FieldHighlightings highlightings);

        /// <summary>
        ///     Adds matches highlighting for the specified field.
        /// </summary>
        /// <remarks>
        ///     The specified field should be analyzed and stored for highlighter to work.
        ///     For each match it creates a fragment that contains matched text surrounded by highlighter tags.
        /// </remarks>
        /// <param name="propertySelector">The property to highlight.</param>
        /// <param name="fragmentLength">The fragment length.</param>
        /// <param name="fragmentCount">The maximum number of fragments for the field.</param>
        /// <param name="fragmentsPropertySelector">The property to put highlights into.</param>
        TSelf Highlight<TValue>(Expression<Func<T, TValue>> propertySelector, int fragmentLength, int fragmentCount, Expression<Func<T, IEnumerable>> fragmentsPropertySelector);

        /// <summary>
        ///     Adds matches highlighting for the specified field.
        /// </summary>
        /// <remarks>
        ///     The specified field should be analyzed and stored for highlighter to work.
        ///     For each match it creates a fragment that contains matched text surrounded by highlighter tags.
        /// </remarks>
        /// <param name="propertySelector">The property to highlight.</param>
        /// <param name="fragmentLength">The fragment length.</param>
        /// <param name="fragmentCount">The maximum number of fragments for the field.</param>
        /// <param name="highlightings">Field highlights for all results.</param>
        TSelf Highlight<TValue>(Expression<Func<T, TValue>> propertySelector, int fragmentLength, int fragmentCount, out FieldHighlightings highlightings);

        /// <summary>
        ///     Adds matches highlighting for the specified field on a Map/Reduce Index.
        /// </summary>
        /// <remarks>
        ///     This is only valid for Map/Reduce Index queries.
        ///     The specified fields should be analyzed and stored for highlighter to work.
        ///     For each match it creates a fragment that contains matched text surrounded by highlighter tags.
        /// </remarks>
        /// <param name="propertySelector">The property to highlight.</param>
        /// <param name="keyPropertySelector">The key property to associate highlights with.</param>
        /// <param name="fragmentLength">The fragment length.</param>
        /// <param name="fragmentCount">The maximum number of fragments for the field.</param>
        /// <param name="highlightings">Field highlights for all results.</param>
        TSelf Highlight<TValue>(Expression<Func<T, TValue>> propertySelector, Expression<Func<T, TValue>> keyPropertySelector, int fragmentLength, int fragmentCount, out FieldHighlightings highlightings);

        /// <summary>
        ///     Includes the specified path in the query, loading the document specified in that path
        /// </summary>
        TSelf Include(string path);

        /// <summary>
        ///     Includes the specified path in the query, loading the document specified in that path
        /// </summary>
        /// <param name="path">The path.</param>
        TSelf Include(Expression<Func<T, object>> path);

        /// <summary>
        ///     Partition the query so we can intersect different parts of the query
        ///     across different index entries.
        /// </summary>
        TSelf Intersect();



        /// <summary>
        ///     Negate the next operation
        /// </summary>
        void NegateNext();

        /// <summary>
        ///     Simplified method for opening a new clause within the query
        /// </summary>
        /// <returns></returns>
        TSelf OpenSubclause();

        /// <summary>
        ///     Add an OR to the query
        /// </summary>
        TSelf OrElse();

        /// <summary>
        ///     Order the results by the specified fields
        ///     The field is the name of the field to sort, defaulting to sorting by ascending.
        /// </summary>
        TSelf OrderBy(string field, OrderingType ordering = OrderingType.String);

        /// <summary>
        ///     Order the results by the specified fields
        ///     The field is the name of the field to sort, defaulting to sorting by ascending.
        /// </summary>
        /// <param name="propertySelectors">Property selectors for the fields.</param>
        TSelf OrderBy<TValue>(params Expression<Func<T, TValue>>[] propertySelectors);

        /// <summary>
        ///     Order the results by the specified fields
        ///     The field is the name of the field to sort, defaulting to sorting by descending.
        /// </summary>
        TSelf OrderByDescending(string field, OrderingType ordering = OrderingType.String);

        /// <summary>
        ///     Order the results by the specified fields
        ///     The field is the name of the field to sort, defaulting to sorting by descending.
        /// </summary>
        /// <param name="propertySelectors">Property selectors for the fields.</param>
        TSelf OrderByDescending<TValue>(params Expression<Func<T, TValue>>[] propertySelectors);

        /// <summary>
        ///     Adds an ordering by score for a specific field to the query
        /// </summary>
        TSelf OrderByScore();

        /// <summary>
        ///     Adds an ordering by score for a specific field to the query
        /// </summary>
        TSelf OrderByScoreDescending();

        /// <summary>
        ///     Specifies a proximity distance for the phrase in the last where clause
        /// </summary>
        /// <param name="proximity">number of words within</param>
        /// <returns></returns>
        /// <remarks>
        ///     http://lucene.apache.org/java/2_4_0/queryparsersyntax.html#Proximity%20Searches
        /// </remarks>
        TSelf Proximity(int proximity);

        /// <summary>
        ///     Order the search results randomly
        /// </summary>
        TSelf RandomOrdering();

        /// <summary>
        ///     Order the search results randomly using the specified seed
        ///     this is useful if you want to have repeatable random queries
        /// </summary>
        TSelf RandomOrdering(string seed);

        /// <summary>
        /// Order the search results randomly
        /// </summary>
        TSelf CustomSortUsing(string typeName, bool descending);

        /// <summary>
        ///     Perform a search for documents which fields that match the searchTerms.
        ///     If there is more than a single term, each of them will be checked independently.
        /// </summary>
        /// <param name="fieldName">Marks a field in which terms should be looked for</param>
        /// <param name="searchTerms">
        ///     Space separated terms e.g. 'John Adam' means that we will look in selected field for 'John'
        ///     or 'Adam'.
        /// </param>
        TSelf Search(string fieldName, string searchTerms, SearchOperator @operator = SearchOperator.Or);

        /// <summary>
        ///     Perform a search for documents which fields that match the searchTerms.
        ///     If there is more than a single term, each of them will be checked independently.
        /// </summary>
        /// <param name="propertySelector">Expression marking a field in which terms should be looked for</param>
        /// <param name="searchTerms">
        ///     Space separated terms e.g. 'John Adam' means that we will look in selected field for 'John'
        ///     or 'Adam'.
        /// </param>
        TSelf Search<TValue>(Expression<Func<T, TValue>> propertySelector, string searchTerms, SearchOperator @operator = SearchOperator.Or);

        /// <summary>
        ///     Sets the tags to highlight matches with.
        /// </summary>
        /// <param name="preTag">Prefix tag.</param>
        /// <param name="postTag">Postfix tag.</param>
        TSelf SetHighlighterTags(string preTag, string postTag);

        /// <summary>
        ///     Sets the tags to highlight matches with.
        /// </summary>
        /// <param name="preTags">Prefix tags.</param>
        /// <param name="postTags">Postfix tags.</param>
        TSelf SetHighlighterTags(string[] preTags, string[] postTags);

        /// <summary>
        ///     This function exists solely to forbid in memory where clause on IDocumentQuery, because
        ///     that is nearly always a mistake.
        /// </summary>
        [Obsolete(@"
You cannot issue an in memory filter - such as Where(x=>x.Name == ""Ayende"") - on IDocumentQuery.
This is likely a bug, because this will execute the filter in memory, rather than in RavenDB.
Consider using session.Query<T>() instead of session.DocumentQuery<T>. The session.Query<T>() method fully supports LINQ queries, while session.DocumentQuery<T>() is intended for lower level API access.
If you really want to do in memory filtering on the data returned from the query, you can use: session.DocumentQuery<T>().ToList().Where(x=>x.Name == ""Ayende"")
", true)]
        IEnumerable<T> Where(Func<T, bool> predicate);

        /// <summary>
        ///     Filter the results from the index using the specified where clause.
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="whereClause">Lucene-syntax based query predicate.</param>
        TSelf WhereLucene(string fieldName, string whereClause);

        /// <summary>
        ///     Matches fields where the value is between the specified start and end, exclusive
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="start">The start.</param>
        /// <param name="end">The end.</param>
        TSelf WhereBetween(string fieldName, object start, object end, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is between the specified start and end, exclusive
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="start">The start.</param>
        /// <param name="end">The end.</param>
        TSelf WhereBetween<TValue>(Expression<Func<T, TValue>> propertySelector, TValue start, TValue end, bool exact = false);

        /// <summary>
        ///     Matches fields which ends with the specified value.
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereEndsWith(string fieldName, object value);

        /// <summary>
        ///     Matches fields which ends with the specified value.
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereEndsWith<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value);

        /// <summary>
        ///     Matches value
        /// </summary>
        TSelf WhereEquals(string fieldName, object value, bool exact = false);

        /// <summary>
        ///     Matches value
        /// </summary>
        TSelf WhereEquals<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value, bool exact = false);

        /// <summary>
        ///     Matches value
        /// </summary>
        TSelf WhereEquals(WhereParams whereParams);

        /// <summary>
        ///     Not matches value
        /// </summary>
        TSelf WhereNotEquals(string fieldName, object value, bool exact = false);

        /// <summary>
        ///     Not matches value
        /// </summary>
        TSelf WhereNotEquals<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value, bool exact = false);

        /// <summary>
        ///     Not matches value
        /// </summary>
        TSelf WhereNotEquals(WhereParams whereParams);

        /// <summary>
        ///     Matches fields where the value is greater than the specified value
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereGreaterThan(string fieldName, object value, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is greater than the specified value
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereGreaterThan<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is greater than or equal to the specified value
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereGreaterThanOrEqual(string fieldName, object value, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is greater than or equal to the specified value
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereGreaterThanOrEqual<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value, bool exact = false);

        /// <summary>
        ///     Check that the field has one of the specified values
        /// </summary>
        TSelf WhereIn(string fieldName, IEnumerable<object> values, bool exact = false);

        /// <summary>
        ///     Check that the field has one of the specified values
        /// </summary>
        TSelf WhereIn<TValue>(Expression<Func<T, TValue>> propertySelector, IEnumerable<TValue> values, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is less than the specified value
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereLessThan(string fieldName, object value, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is less than the specified value
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereLessThan<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is less than or equal to the specified value
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereLessThanOrEqual(string fieldName, object value, bool exact = false);

        /// <summary>
        ///     Matches fields where the value is less than or equal to the specified value
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereLessThanOrEqual<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value, bool exact = false);

        /// <summary>
        ///     Matches fields which starts with the specified value.
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereStartsWith(string fieldName, object value);

        /// <summary>
        ///     Matches fields which starts with the specified value.
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="value">The value.</param>
        TSelf WhereStartsWith<TValue>(Expression<Func<T, TValue>> propertySelector, TValue value);

        /// <summary>
        ///     Check if the given field exists
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        TSelf WhereExists<TValue>(Expression<Func<T, TValue>> propertySelector);

        /// <summary>
        ///     Check if the given field exists
        /// </summary>
        /// <param name="fieldName">Name of the field.</param>
        TSelf WhereExists(string fieldName);

        /// <summary>
        ///     Filter matches to be inside the specified radius
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="radius">Radius (measured in units passed to radiusUnits parameter) in which matches should be found.</param>
        /// <param name="latitude">Latitude pointing to a circle center.</param>
        /// <param name="longitude">Longitude pointing to a circle center.</param>
        /// <param name="radiusUnits">Units that will be used to measure distances (Kilometers, Miles).</param>
        TSelf WithinRadiusOf<TValue>(Expression<Func<T, TValue>> propertySelector, double radius, double latitude, double longitude, SpatialUnits? radiusUnits = null, double distanceErrorPct = Constants.Documents.Indexing.Spatial.DefaultDistanceErrorPct);

        /// <summary>
        ///     Filter matches to be inside the specified radius
        /// </summary>
        /// <param name="fieldName">Spatial field name.</param>
        /// <param name="radius">Radius (measured in units passed to radiusUnits parameter) in which matches should be found.</param>
        /// <param name="latitude">Latitude pointing to a circle center.</param>
        /// <param name="longitude">Longitude pointing to a circle center.</param>
        /// <param name="radiusUnits">Units that will be used to measure distances (Kilometers, Miles).</param>
        TSelf WithinRadiusOf(string fieldName, double radius, double latitude, double longitude, SpatialUnits? radiusUnits = null, double distanceErrorPct = Constants.Documents.Indexing.Spatial.DefaultDistanceErrorPct);

        /// <summary>
        ///     Filter matches based on a given shape - only documents with the shape defined in fieldName that
        ///     have a relation rel with the given shapeWKT will be returned
        /// </summary>
        /// <param name="propertySelector">Property selector for the field.</param>
        /// <param name="shapeWKT">WKT formatted shape</param>
        /// <param name="relation">Spatial relation to check (Within, Contains, Disjoint, Intersects, Nearby)</param>
        /// <param name="distanceErrorPct">The allowed error percentage. By default: 0.025</param>
        TSelf RelatesToShape<TValue>(Expression<Func<T, TValue>> propertySelector, string shapeWKT, SpatialRelation relation, double distanceErrorPct = Constants.Documents.Indexing.Spatial.DefaultDistanceErrorPct);

        /// <summary>
        ///     Filter matches based on a given shape - only documents with the shape defined in fieldName that
        ///     have a relation rel with the given shapeWKT will be returned
        /// </summary>
        /// <param name="fieldName">Spatial field name.</param>
        /// <param name="shapeWKT">WKT formatted shape</param>
        /// <param name="relation">Spatial relation to check (Within, Contains, Disjoint, Intersects, Nearby)</param>
        /// <param name="distanceErrorPct">The allowed error percentage. By default: 0.025</param>
        TSelf RelatesToShape(string fieldName, string shapeWKT, SpatialRelation relation, double distanceErrorPct = Constants.Documents.Indexing.Spatial.DefaultDistanceErrorPct);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistance<TValue>(Expression<Func<T, TValue>> propertySelector, double latitude, double longitude);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistance(string fieldName, double latitude, double longitude);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistance<TValue>(Expression<Func<T, TValue>> propertySelector, string shapeWkt);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistance(string fieldName, string shapeWkt);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistanceDescending<TValue>(Expression<Func<T, TValue>> propertySelector, double latitude, double longitude);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistanceDescending(string fieldName, double latitude, double longitude);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistanceDescending<TValue>(Expression<Func<T, TValue>> propertySelector, string shapeWkt);

        /// <summary>
        /// Sorts the query results by distance.
        /// </summary>
        TSelf OrderByDistanceDescending(string fieldName, string shapeWkt);
     */
}
