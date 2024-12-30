import { UserResponse } from "../user/user-response.model";

export interface ProjectResponse {
    id: number;               // Project ID
    name: string;             // Project Name
    description: string;      // Project Description
    startDate: Date;        // Project Start Date (used as Created Date)
    endDate : Date | null ;
    owner: UserResponse | null;     // Owner ID (already exists)
  }
  