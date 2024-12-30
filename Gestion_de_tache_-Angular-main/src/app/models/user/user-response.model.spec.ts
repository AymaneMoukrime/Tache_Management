import { UserResponse } from './user-response.model';

describe('UserResponse', () => {
  it('should create a valid user object', () => {
    const user = new UserResponse(
      1, 
      'John Doe', 
      'john.doe@example.com', 
       new Date('2025-10-10'),
      'ADMIN'
    );

    expect(user).toBeTruthy(); // Checks that the object exists
    expect(user.name).toEqual('John Doe'); // Additional property check
    expect(user.email).toEqual('john.doe@example.com');
    expect(user.dateInscription).toBeInstanceOf(Date); // Ensure it's a Date object
    expect(user.dateInscription.toISOString()).toEqual('2025-10-10T00:00:00.000Z');
  });
});
