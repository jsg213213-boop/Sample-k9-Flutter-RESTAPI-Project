export interface Member {
  id: number;
  mid: string;
  mname: string;
  email?: string;
  region?: string;
  role?: string;
  profileImg?: string;
  regDate?: string;
}

export interface MemberSignupPayload {
  mid: string;
  mpw: string;
  mpwConfirm: string;
  mname: string;
  email: string;
  region?: string;
  profileImageBase64?: string;
}
