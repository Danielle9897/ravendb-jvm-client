﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Raven.Client.Document;
using Raven.Client.Shard;
using Raven.Client;

namespace Raven.Sample.ShardClient
{
    class Program
    {
        static void Main(string[] args)
        {
            var shards = new Shards { 
                new DocumentStore { Identifier="Shard1", Url = "http://localhost:8080" }, 
                new DocumentStore { Identifier="Shard2", Url = "http://localhost:8081" } 
            };

            using (var documentStore = new ShardedDocumentStore(new ShardStrategy(), shards).Initialise())
            using (var session = documentStore.OpenSession())
            {
                //store 2 items in the 2 shards
                session.Store(new Company { Name = "Company 1", Region = "A" });
                session.Store(new Company { Name = "Company 2", Region = "B" });
                session.SaveChanges();

                //get all, should automagically retrieve from each shard
                var allCompanies = session.Query<Company>().WaitForNonStaleResults().ToArray();

                foreach (var company in allCompanies)
                    Console.WriteLine(company.Name);
            }
        }

        static void Main2(string[] args)
        {
			using (var documentStore = new DocumentStore { Url = "http://localhost:8080" }.Initialise())
            using (var session = documentStore.OpenSession())
            {
                //session.Store(new Company { Name = "Company 1", Region = "A" });
                //session.Store(new Company { Name = "Company 2", Region = "B" });
                //session.SaveChanges();

                var allCompanies = session
                    .Query<Company>("regionIndex")
                    .Where("Region:A")
                    .WaitForNonStaleResults()
                    .ToArray();

                foreach (var company in allCompanies)
                    Console.WriteLine(company.Name);
            }
        }

    }
}