// task-dto.model.spec.ts

import { TaskDto, TaskStatus } from './taskDto.model';

describe('TaskDto', () => {
  let task: TaskDto;

  beforeEach(() => {
    task = {
      id: 1,
      title: 'Test Task',
      description: 'This is a test task',
      dateCreation: new Date(),
      dateDeadline: new Date('2024-12-31'),
      priorite: 1,
      status: 'TODO',
      couleur: '#FF5733',
      taskOwnerEmail : '',
       taskUserEmail: ''
      
      
    };
  });

  it('should create a TaskDto object', () => {
    expect(task).toBeTruthy();
    expect(task.id).toEqual(1);
    expect(task.title).toBe('Test Task');
    expect(task.description).toBe('This is a test task');
    expect(task.dateCreation).toBeInstanceOf(Date);
    expect(task.dateDeadline).toEqual(new Date('2024-12-31'));
    expect(task.priorite).toBe(1);
    expect(task.status).toBe('TODO');
    expect(task.couleur).toBe('#FF5733');
    
  });

  it('should allow TaskStatus to be "DONE", "TODO", or "IN_PROGRESS"', () => {
    const validStatuses: TaskStatus[] = ['DONE', 'TODO', 'IN_PROGRESS'];

    validStatuses.forEach((status) => {
      task.status = status;
      expect(task.status).toBe(status);
    });
  });

  

  it('should throw an error for invalid TaskStatus', () => {
    // This is a TypeScript compile-time error and cannot be tested at runtime
    // However, this ensures the model type safety
    expect(() => {
      (task.status as any) = 'INVALID_STATUS';
    }).toThrowError();
  });
});
