export class IResponse<T> {
  static statuscode = { SUCCESS: 0, FAILURE: 1 };

  constructor(parameters) {}
  code: number;
  message: string;
  result: T;
}
