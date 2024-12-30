import { TeamDto } from './teamDto.model';  // Correct path to the TeamDto model
import { UserResponse } from "../user/user-response.model";  // Assuming this model is defined in the user folder

describe('TeamDto Model', () => {
  it('should create a valid TeamDto object', () => {
    const users: UserResponse[] = [
      { id: 1, name: 'John Doe', email: 'john.doe@example.com', dateInscription: new Date('2022-01-01'), userRole: 'NORMAL' }
    ];
    
    const team: TeamDto = {
      id: 1,
      name: 'Development Team',
      dateCreation: new Date('2024-01-01'),
      projectName : '',
      projectId : 0,
      users: users
    };
    
    expect(team).toBeTruthy();
    expect(team.id).toBe(1);
    expect(team.name).toBe('Development Team');
    expect(team.dateCreation).toEqual(new Date('2024-01-01'));
    expect(team.users.length).toBe(1);
    expect(team.users[0].name).toBe('John Doe');
  });

  it('should handle empty users array', () => {
    const team: TeamDto = {
      id: 2,
      name: 'Marketing Team',
      dateCreation: new Date('2024-01-02'),
      projectId : 0,
      projectName : '',
      users: []  // Empty users array
    };
    
    expect(team).toBeTruthy();
    expect(team.id).toBe(2);
    expect(team.name).toBe('Marketing Team');
    expect(team.dateCreation).toEqual(new Date('2024-01-02'));
    expect(team.users.length).toBe(0);
  });
});
