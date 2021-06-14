import { Environment } from "./environment";
import { EvidenceType } from "./evidence-type";
import { Retry } from "./retry";
import { TestRepository } from "./test-repository";
import { EvidenceCase } from "./evidence-case";
import { Timeout } from "./timeout";

export class Configuration {
  processType: string;
  configurationId: number;
  active: boolean;
  name: string;
  uploadResults: boolean;
  clientEnvironment: Environment;
  retry: Retry;
  timeout: Timeout;
  evidenceTypes: EvidenceType[];
  testRepository: TestRepository;
  evidenceCase : EvidenceCase;

  constructor() {
    this.processType = "SQA";
    this.configurationId = 0;
    this.active = true;
    this.name = "";
    this.uploadResults = false;
    this.clientEnvironment = new Environment();
    this.retry = new Retry();
    this.timeout = new Timeout();
    this.evidenceTypes = [];
    this.testRepository = new TestRepository();
    this.evidenceCase = new EvidenceCase();
  }
}
