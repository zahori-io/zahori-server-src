export interface ApiResponse<T> {
    statusCode: number;
    status: string;
    message: string;
    data: T;
}