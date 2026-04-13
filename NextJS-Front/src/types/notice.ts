export interface NoticeImage {
  id?: number;
  fileName: string;
  uuid: string;
  ord: number;
}

export interface Notice {
  id: number;
  title: string;
  content: string;
  writer?: string;
  topFixed?: boolean;
  regDate?: string;
  images?: NoticeImage[];
}
