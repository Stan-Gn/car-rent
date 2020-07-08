# car-rent
Final project of the SDAcedemy course. Made individually. The purpose of creating the application is to learn about selected projects within the Spring platform. 
The application has been deployed on https://carrent-app.herokuapp.com/ with database Postgres – SQL

# illustrations - database model
![image](https://user-images.githubusercontent.com/55624761/86899583-7f6e7300-c10a-11ea-8553-a58c6f83d016.png)


# launching application
Input data needed for application operation - data loaded into the H2 database, loaded from the DataLoader class - car list with pictures, list of users and rental list. For the function to send an email with an activation link to work properly, enter username and password of the email in application.properties.

# technologies
  *	Spring Boot, 
  *	Spring Security, 
  *	Spring Data, 
  *	Spring MVC  
  *	Lombok
  *	SQL

# functionality
  *	User registration using the account activation link sent to the email.
  *	Access to the account by login and password.
  *	Users are saved to the database.
  *	Displayed list of cars from database. Possibility of sorting and pagination of results, filtring available cars by date.
  *	Car reservation is possible only after authentication.
  *	Administration panel – access only with the role of admin.
    -	List of users and the ability to be blocked account by admin.
    -	List of cars and the feature of adding a new car with a photo via the form - saving to the database.
    -	List of rentals, feature of approving a car rent and cost calculation

# sources
Website template: https://www.templateshub.net/template/renten-car-rental-template
