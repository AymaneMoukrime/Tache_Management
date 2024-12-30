import { ProjectResponse } from './project-response.model';
import { UserResponse } from '../user/user-response.model';

describe('ProjectResponse Interface', () => {
  it('should create a valid ProjectResponse object', () => {
    const project: ProjectResponse = {
      id: 1,
      name: "ahmed project number 1",
      description: "this is project of ahmed",
      startDate: new Date("2024-12-18"),
      endDate: new Date("2024-12-19"),  // Adding the missing endDate
      owner: null // You can set this to null or create a mock UserResponse if needed
    };

    expect(project).toBeTruthy();
    expect(project.id).toBe(1);
    expect(project.name).toBe('ahmed project number 1');
    expect(project.description).toBe('this is project of ahmed');
    expect(project.startDate).toEqual(new Date("2024-12-18"));
    expect(project.endDate).toEqual(new Date("2024-12-19"));
    expect(project.owner).toBeNull(); // Testing for null owner or you can mock a user here
  });
});
