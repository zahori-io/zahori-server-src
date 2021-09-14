import { Attachment } from "./attachment";
import { Browser } from "./browser";
import { Case } from "./case";
import { Step } from "./step";

export class CaseExecution {
    caseExecutionId: number;
    date: string;
    log: string;
    notes: string;
    status: string;
    steps: string;
    stepsJson: Step[];
    video: string;
    doc: string;
    har: string;
    attachments: Attachment[];
    durationSeconds: number;
    browser: Browser;
    cas: Case;
    selenoidId: string;
    screenResolution: string;
    browserVersion: string;
}