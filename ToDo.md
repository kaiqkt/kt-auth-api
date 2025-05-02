## Role Service Features

- [x] Implement the `create` method to create a new role if it does not already exist.
- [x] Implement the `findAll` method to retrieve a paginated list of roles filtered by a search term.
- [x] Implement the `findAllById` method to retrieve a list of roles by their IDs.
- [x] Implement the `deleteAllById` method to delete roles by their IDs and detach the role from the users that are associated.
- [x] Implement the `update` method to update a role description by ID.

## User Service Features

- [x] Implement the `create` method to create a new user if the email does not already exist.
- [x] Implement the `addRole` to add role(s) in the user.
- [x] Implement the `findById` method to retrieve a user by ID and handle the case where the user is not found.
- [x] Implement the `removeRoles` method to remove a role(s) from a user.
- [x] Implement the `findAll` method to retrieve a paginated list of users filtered by roles and status.
- [x] Implement the `findById` method to retrieve the use by user id given in the access token.
- [x] Implement the `updatePassword` method to update a user password.
- [x] Implement the `resetPassword` method to send a email with a code to reset the password.
- [x] Implement the `verify` method to verify a user sending him a email.

## Session Service Features
- [x] Implement the `create` method to create a new session.
- [x] Implement the `revoke` method to invalidate a session.
- [x] Implement the `revokeAll` method to invalidate all sessions of a user.
- [x] Implement the `revokeById` method to invalidate a session by ID.
- [x] Implement the `findAll` method to retrieve a paginated list of sessions filtered by user ID.
- [x] Implement the `findAll` method to retrieve a paginated list of sessions filtered by user access token.

## Authentication Features

- [x] Implement the `login` method to authenticate a user and return a session.
- [x] Implement the `refresh` method to refresh a session.
- [x] Implement the `verify` method to verify a session.

## Enhancements

- [x] Limitation on pagination size to 999
- [x] Indexing on the database
- [x] Name in roles be unique
- [x] Remove all unused commentaries
- [x] Use ErrorV1 from the open api code
- [x] Insert default role user
- [x] Improve performance for login for when need to send the verify email ASYNC
- [x] Improve verification service to a control better the send of a email(auth-registry/communication-service)
- [x] Create and push to the git repository
- [x] Reset password screen to pass the new password
- [x] Implement delete account

## Documentation
- [X] Document API endpoints
- [x] Write a guide for deploying the application
