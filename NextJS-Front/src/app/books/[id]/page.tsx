"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import axios from "axios";
import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Book } from "@/types/book";

/**
 * 도서 상세 + 대여 신청 — GET/POST /api/book/{id}, POST /api/rental
 */
export default function BookDetailPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const { member } = useAuth();

  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [renting, setRenting] = useState(false);
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const res = await api.get<Book>(`/book/${params.id}`);
        setBook(res.data);
      } catch (err: unknown) {
        setError(
          err instanceof Error ? err.message : "도서를 불러오지 못했습니다.",
        );
      } finally {
        setLoading(false);
      }
    })();
  }, [params.id]);

  const handleRent = async () => {
    if (!member) {
      router.push("/login");
      return;
    }
    if (!book) return;
    setRenting(true);
    setMsg(null);
    try {
      const res = await api.post<{ message?: string }>("/rental", {
        memberId: member.id,
        bookId: book.id,
      });
      setMsg(res.data.message ?? "대여 신청이 완료되었습니다.");
    } catch (err: unknown) {
      let m = "대여 신청에 실패했습니다.";
      if (axios.isAxiosError(err)) {
        const data = err.response?.data as { message?: string } | undefined;
        if (data?.message) m = data.message;
      }
      setMsg(m);
    } finally {
      setRenting(false);
    }
  };

  if (loading) {
    return (
      <main className="mx-auto max-w-3xl p-6">
        <p className="text-gray-500">로딩 중...</p>
      </main>
    );
  }
  if (error || !book) {
    return (
      <main className="mx-auto max-w-3xl p-6">
        <p className="rounded bg-red-50 p-3 text-sm text-red-600">
          {error ?? "도서를 찾을 수 없습니다."}
        </p>
        <Link href="/books" className="mt-4 inline-block text-brand-600">
          ← 목록으로
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-3xl p-6">
      <Link href="/books" className="text-sm text-brand-600 hover:underline">
        ← 도서 목록
      </Link>

      <div className="mt-4 rounded-lg bg-white p-6 shadow">
        {book.coverImage && (
          // eslint-disable-next-line @next/next/no-img-element
          <img
            src={book.coverImage}
            alt={book.bookTitle}
            className="mb-4 max-h-96 w-full rounded object-contain"
          />
        )}
        <h1 className="text-2xl font-bold">{book.bookTitle}</h1>
        <p className="mt-1 text-gray-700">저자: {book.author}</p>
        {book.publisher && (
          <p className="mt-1 text-sm text-gray-500">출판사: {book.publisher}</p>
        )}
        {book.isbn && (
          <p className="mt-1 text-xs text-gray-400">ISBN: {book.isbn}</p>
        )}
        {book.description && (
          <p className="mt-4 whitespace-pre-wrap text-sm text-gray-700">
            {book.description}
          </p>
        )}

        <div className="mt-6 flex gap-3">
          <button
            onClick={handleRent}
            disabled={renting}
            className="rounded bg-brand-600 px-4 py-2 text-white hover:bg-brand-700 disabled:bg-gray-400"
          >
            {renting ? "신청 중..." : "대여 신청"}
          </button>
        </div>
        {msg && (
          <p className="mt-4 rounded bg-blue-50 p-3 text-sm text-blue-700">
            {msg}
          </p>
        )}
      </div>
    </main>
  );
}
