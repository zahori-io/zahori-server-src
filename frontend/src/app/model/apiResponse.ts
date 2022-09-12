import { Page } from "./page";

export interface ApiResponse<T> {
    statusCode: number;
    status: string;
    message: string;
    data: T;
}