export class Step {
    id: string;
    attachments: string[] = [];
    customFields: Map<string, string> = new Map();
    name: string;
    status: string;
    description: string;
    descriptionArgs: string[];
    expected: string;
    actual: string;
    executionTime: string;
    runId: string;
    passed: boolean;
    messageText: string;
    subSteps: Step[];
}