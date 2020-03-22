An example for graphql-java-kickstart with data loader

1. Download or 'git clone' this project

2. Startup this spring-boot application

   + If you want to run it by Intellij, import this project, then open 'src/main/kotlin/org/frchen/graph/example/GraphQLApp.kt' and run it
   + If you want to run it by command line, use "mvn spring-boot:run", or "mvn clean install" and" java -jar target/app.jar"

3. Open your browser, access http://localhost:8080/graphiql. (Becareful, graphiql, not graphql)

4. You can run the GraphQL query by the web page, here is an example of request body

   ```
   {
     example1: findDepartments(name: "d") {
       id
       name
       location
       employees {
         id
         name
         gender
         supervisor {
           id
           name
         }
         subordinates {
           id
           name
         }
       }
     }
     example2: findEmployees(gender: FEMALE) {
       id
       name
       mobile
       supervisor {
         id
         name
         gender
       }
       subordinates {
         id
         name
         gender
       }
       department {
         id
         name
       }
     }
   }
   ```

5. Try to change the request body and execute, and watch the change of SQL log in the server console. (Note: All the primary keys and foreign keys are always selected, other fields are controlled by GraphQL)