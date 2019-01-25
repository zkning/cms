export class SimpleUtils {
  static getSelectedNodeList(ngModel: any) {
    const selectedList = [];
    if (ngModel) {
      ngModel.forEach(element => {
        if (element.isSelected) {
          selectedList.push(element);
        }

        // 非叶子节点,继续遍历
        if (!element.isLeaf) {
          const items = this.getSelectedNodeList(element.children);
          items.forEach(item => {
            selectedList.push(item);
          });
        }
      });
    }
    return selectedList;
  }

  static getCheckedNodeList(ngModel: any) {
    const checkedList = [];
    if (ngModel) {
      ngModel.forEach(element => {
        if (element.isChecked) {
          checkedList.push(element);
        }

        // 非叶子节点,继续遍历
        if (!element.isLeaf) {
          const items = this.getCheckedNodeList(element.children);
          items.forEach(item => {
            checkedList.push(item);
          });
        }
      });
    }
    return checkedList;
  }

  static getCheckedNodes(ngModel: any) {
    const checkedList = [];
    if (ngModel) {
      ngModel.forEach(element => {
        checkedList.push(element);
        // 非叶子节点,继续遍历
        if (!element.isLeaf) {
          const items = this.getCheckedNodeList(element.children);
          items.forEach(item => {
            checkedList.push(item);
          });
        }
      });
    }
    return checkedList;
  }
}
