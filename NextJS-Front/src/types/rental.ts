export interface Rental {
  id: number;
  memberId?: number;
  memberName?: string;
  memberMid?: string;
  bookId?: number;
  bookTitle?: string;
  bookAuthor?: string;
  rentalDate?: string;
  dueDate?: string;
  returnDate?: string;
  status?: string;
  overdue?: boolean;
}
