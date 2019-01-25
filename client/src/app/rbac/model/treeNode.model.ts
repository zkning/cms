export class TreeNodeModel {
  constructor(parameters) {}
  title: string;
  isLeaf: boolean;
  disabled: boolean;
  children: Array<TreeNodeModel>;
  selectable: boolean;
}
