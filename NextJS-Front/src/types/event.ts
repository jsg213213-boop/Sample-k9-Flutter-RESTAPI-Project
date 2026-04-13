export interface LibraryEvent {
  id: number;
  category?: string;
  title: string;
  content?: string;
  place?: string;
  maxParticipants?: number;
  currentParticipants?: number;
  remainingSlots?: number;
  status?: string;
  regDate?: string;
}

export interface EventApplication {
  id: number;
  eventId: number;
  eventTitle?: string;
  eventCategory?: string;
  eventPlace?: string;
  memberId?: number;
  memberName?: string;
  applyDate?: string;
  status?: string;
}
