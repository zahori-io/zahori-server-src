import { Tag } from "./tag";

export class Case {
  caseId: number;
  active: boolean;
  name: string;
  clientTags: Tag[];
  data: Map<string, string> = new Map();
  selected: boolean;
  
  constructor() {
    this.selected = false;
  }
}
