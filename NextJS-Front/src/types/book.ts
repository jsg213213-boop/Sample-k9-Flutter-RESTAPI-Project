export interface Book {
  id: number;
  bookTitle: string;
  author: string;
  publisher?: string;
  isbn?: string;
  description?: string;
  coverImage?: string;
  regDate?: string;
}
