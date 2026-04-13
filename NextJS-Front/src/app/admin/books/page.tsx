"use client";

import { useCallback, useEffect, useState, FormEvent } from "react";
import axios from "axios";
import Link from "next/link";
import Protected from "@/components/Protected";
import { api, PageResponse } from "@/lib/api";
import { Book } from "@/types/book";

export default function AdminBooksPage() {
  return (
    <Protected requireRole="ADMIN">
      <Inner />
    </Protected>
  );
}

function Inner() {
  const [books, setBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState<string | null>(null);

  const [bookTitle, setBookTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [publisher, setPublisher] = useState("");
  const [isbn, setIsbn] = useState("");
  const [description, setDescription] = useState("");

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get<PageResponse<Book>>("/book", {
        params: { page: 0, size: 50 },
      });
      setBooks(res.data.content ?? []);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchBooks();
  }, [fetchBooks]);

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/book", {
        bookTitle,
        author,
        publisher: publisher || undefined,
        isbn: isbn || undefined,
        description: description || undefined,
      });
      setMsg("등록되었습니다.");
      setBookTitle("");
      setAuthor("");
      setPublisher("");
      setIsbn("");
      setDescription("");
      fetchBooks();
    } catch (err: unknown) {
      let m = "등록 실패";
      if (axios.isAxiosError(err)) {
        const d = err.response?.data as { message?: string } | undefined;
        if (d?.message) m = d.message;
      }
      setMsg(m);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("삭제하시겠습니까?")) return;
    try {
      await api.delete(`/book/${id}`);
      setMsg("삭제되었습니다.");
      fetchBooks();
    } catch (err: unknown) {
      let m = "삭제 실패";
      if (axios.isAxiosError(err)) {
        const d = err.response?.data as { message?: string } | undefined;
        if (d?.message) m = d.message;
      }
      setMsg(m);
    }
  };

  return (
    <main className="mx-auto max-w-5xl p-6">
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-bold">도서 관리</h1>
        <Link href="/admin" className="text-sm text-brand-600">
          ← 대시보드
        </Link>
      </div>

      {msg && (
        <p className="mb-3 rounded bg-blue-50 p-3 text-sm text-blue-700">
          {msg}
        </p>
      )}

      <form
        onSubmit={handleCreate}
        className="mb-6 grid grid-cols-1 gap-2 rounded bg-white p-4 shadow-sm sm:grid-cols-2"
      >
        <input
          required
          placeholder="도서명"
          value={bookTitle}
          onChange={(e) => setBookTitle(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <input
          required
          placeholder="저자"
          value={author}
          onChange={(e) => setAuthor(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <input
          placeholder="출판사"
          value={publisher}
          onChange={(e) => setPublisher(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <input
          placeholder="ISBN"
          value={isbn}
          onChange={(e) => setIsbn(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <textarea
          placeholder="설명"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={2}
          className="col-span-full rounded border px-3 py-2"
        />
        <button
          type="submit"
          className="col-span-full rounded bg-brand-600 py-2 font-semibold text-white hover:bg-brand-700"
        >
          도서 등록
        </button>
      </form>

      {loading ? (
        <p className="text-gray-500">로딩 중...</p>
      ) : (
        <table className="w-full rounded border bg-white text-sm shadow-sm">
          <thead className="bg-gray-50 text-left">
            <tr>
              <th className="p-2">ID</th>
              <th className="p-2">도서명</th>
              <th className="p-2">저자</th>
              <th className="p-2">출판사</th>
              <th className="p-2">동작</th>
            </tr>
          </thead>
          <tbody>
            {books.map((b) => (
              <tr key={b.id} className="border-t">
                <td className="p-2">{b.id}</td>
                <td className="p-2">{b.bookTitle}</td>
                <td className="p-2">{b.author}</td>
                <td className="p-2">{b.publisher ?? "-"}</td>
                <td className="p-2">
                  <button
                    onClick={() => handleDelete(b.id)}
                    className="rounded border border-red-300 px-2 py-0.5 text-xs text-red-600 hover:bg-red-50"
                  >
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}
