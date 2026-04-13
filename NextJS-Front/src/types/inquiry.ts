export interface Reply {
  id: number;
  replyText: string;
  replier?: string;
  inquiryId: number;
  regDate?: string;
}

export interface Inquiry {
  id: number;
  title: string;
  content: string;
  writer?: string;
  memberId?: number;
  answered?: boolean;
  secret?: boolean;
  regDate?: string;
  replies?: Reply[];
}
