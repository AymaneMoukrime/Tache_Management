import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TeamDto } from '../../models/team/teamDto.model';
import { TaskDto } from '../../models/task/taskDto.model';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8082/api/task';  // Adjust URL accordingly

  constructor(private http: HttpClient) {}
    
       /**
       * Retrieves the Authorization headers safely.
       */
       private getAuthHeaders(): HttpHeaders {
        let token = '';
        if (typeof window !== 'undefined') {
          // Guard for SSR: Ensure code runs in the browser
          token = localStorage.getItem('token') || '';
        }
        console.log('Authorization Token:', token);
    
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        
        return headers;
      }

  getTask(projectId: number): Observable<TaskDto[]> {
    const headers =  this.getAuthHeaders();
    return this.http.get<TaskDto[]>(`${this.apiUrl}/taskinfo/${projectId}`,{headers, responseType: 'json' });
  }

  getTaskById(projectId: number, taskId: number): Observable<TaskDto> {
    const headers = this.getAuthHeaders();
    return this.http.get<TaskDto>(`${this.apiUrl}/taskinfo/${projectId}/${taskId}`, { headers , responseType: 'json' });
  }
  
  updateTask(taskId: number, task: TaskDto): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.put(`${this.apiUrl}/updateTask/${taskId}`, task, { headers , responseType: 'json' });
  }
  
  assignTask(taskId: number, userEmail: string): Observable<any> {
    const headers = this.getAuthHeaders();
    const url = `${this.apiUrl}/AssignTask/${taskId}?Email=${encodeURIComponent(userEmail)}`;
    return this.http.post<any>(url, {}, { headers , responseType: 'json' });
  }
  setTaskStatus(taskId: number, status: string): Observable<any> {
    const headers = this.getAuthHeaders();
    const url = `${this.apiUrl}/setstatus/${taskId}/${status}`;
    return this.http.put(url, {}, { headers, responseType: 'json' });
  }

  createTask(projectId: number, task: { titre: string; description: string; priorite: number; couleur: string }): Observable<any> {
    const headers = this.getAuthHeaders();
    const url = `${this.apiUrl}/createTask/${projectId}`;
    return this.http.post<any>(url, task, { headers });
  }
  
  deleteTask(projectId: number, taskId: number): Observable<void> {
    const url = `${this.apiUrl}/deletetask/${taskId}`;
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(url, { headers , responseType: 'json' });
  }
  
  
  
}
