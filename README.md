A GraphQL server-side example by using graphql-kotlin(https://github.com/ExpediaGroup/graphql-kotlin) with data loader

This is server-side demo for JVM, please visit https://github.com/babyfish-ct/graphql-csharp-example to see the server-side demo for .NET Core.

# Start server

1. Download or 'git clone' this project

2. Startup this spring-boot application

   + If you want to run it by Intellij, import this project, then open 'src/main/kotlin/com/citicguoan/training/App.kt' and run it
   + If you want to run it by command line, use "mvn spring-boot:run", or "mvn clean install" and" java -jar target/app.jar"

3. Open your browser, access http://localhost:8080/playground

   The query operations can be used by everyone, but the mutation operations can only be used by the authorized user.
   You can execute the mutation operations like this

   a. Execute the query to get the token

   ```
   query {
       login(loginName: "admin", password: "123") {
           token
       }
   }
   ```
   The response should be
   ```
   {
       "data": {
       "login": {
         "token": "<<token text>>"
       }
     }
   }
   ```
   Copy the &lt;&lt;token text&gt;&gt; to clipboard

   b. Enter mutation operation

   c. Use the token the execute mutation exception
   There is a button whose text is 'HTTP HEADERS' on the query operations UI of playgound, 
   click it, a textarea that is used to edit http headers will appear, edit it like this

   ```
   {
       "Authorization": "<<token text>>"
   }
   ```
   Then the mutation operation can be execute successfully.

# Start client

please see http://github.com/babyfish-ct/graphql-react-example to know more