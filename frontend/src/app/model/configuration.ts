import { Environment } from "./environment";
import { EvidenceType } from "./evidence-type";
import { Retry } from "./retry";
import { EvidenceCase } from "./evidence-case";
import { Timeout } from "./timeout";
import { ClientTestRepo } from "./clientTestRepo";

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
  clientTestRepo: ClientTestRepo;
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
    this.clientTestRepo = new ClientTestRepo();
    this.evidenceCase = new EvidenceCase();
  }
}
