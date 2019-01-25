import { Component, OnInit } from '@angular/core';
import { NzModalRef, NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';

@Component({
  selector: 'app-photeview',
  templateUrl: './photeview.component.html',
  styles: [``],
})
export class CreditauditPhoteviewComponent implements OnInit {
  // index ,photoList
  record: any = {};
  i: any;
  transform_style: any;
  deg = 0;
  photo_style: any;
  imgW = 600;
  imgH = 400;
  tempNum = 25;

  // 图片项
  photoItem: any;
  photoIndex: number;
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
  ) {}

  ngOnInit(): void {
    this.photo_style = {
      width: this.imgW + 'px',
      height: this.imgH + 'px',
      border: '1px',
      margin: '0 auto',
    };
    this.photoItem = this.record.photoList[this.record.index];
  }

  next() {
    if (this.record.index >= this.record.photoList.length - 1) {
      return;
    }
    this.init();
    this.record.index = this.record.index + 1;
    this.photoItem = this.record.photoList[this.record.index];
  }

  pre() {
    if (this.record.index <= 0) {
      return;
    }
    this.init();
    this.record.index = this.record.index - 1;
    this.photoItem = this.record.photoList[this.record.index];
  }

  // 证件照片单击旋转
  right($event) {
    this.deg = this.deg + 90;
    if (this.deg > 360) {
      this.deg = 90;
    }
    this.transform_style = {
      transform: 'rotate(' + this.deg + 'deg)',
      height: '100%',
      width: '100%',
      'border-radius': '5px',
    };
  }

  init() {
    this.deg = 0;
    this.transform_style = {
      transform: 'rotate(' + this.deg + 'deg)',
      height: '100%',
      width: '100%',
      'border-radius': '5px',
    };

    this.imgH = 400;
    this.imgW = 600;
    this.photo_style = {
      width: this.imgW + 'px',
      height: this.imgH + 'px',
      border: '1px',
      margin: '0 auto',
    };
  }

  close() {
    this.modal.destroy();
  }

  left($event) {
    this.deg = this.deg - 90;
    if (this.deg < -360) {
      this.deg = -90;
    }
    this.transform_style = {
      transform: 'rotate(' + this.deg + 'deg)',
      height: '100%',
      width: '100%',
      'border-radius': '5px',
    };
  }

  max() {
    this.imgH = this.imgH + this.tempNum;
    this.imgW = this.imgW + this.tempNum;
    this.photo_style = {
      width: this.imgW + 'px',
      height: this.imgH + 'px',
      border: '1px',
      margin: '0 auto',
    };
  }

  min() {
    if (this.imgH - this.tempNum >= 0) {
      this.imgH = this.imgH - this.tempNum;
    }

    if (this.imgW - this.tempNum >= 0) {
      this.imgW = this.imgW - this.tempNum;
    }
    this.photo_style = {
      width: this.imgW + 'px',
      height: this.imgH + 'px',
      border: '1px',
      margin: '0 auto',
    };
  }
}
