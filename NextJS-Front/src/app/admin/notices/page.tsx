"use client";

import { useCallback, useEffect, useState, FormEvent } from "react";
import axios from "axios";
import Link from "next/link";
import Protected from "@/components/Protected";
import { api, PageResponse } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Notice } from "@/types/notice";

export default function AdminNoticesPage() {
  return (
    <Protected requireRole="ADMIN">
      <Inner />
    </Protected>
  );
}

function Inner() {
  const { member } = useAuth();
  const [notices, setNotices] = useState<Notice[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState<string | null>(null);

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [topFixed, setTopFixed] = useState(false);

  const fetchNotices = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get<PageResponse<Notice>>("/notice", {
        params: { page: 0, size: 50 },
      });
      setNotices(res.data.content ?? []);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchNotices();
  }, [fetchNotices]);

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/notice", {
        title,
        content,
        writer: member?.mname ?? "관리자",
        topFixed,
      });
      setMsg("등록되었습니다.");
      setTitle("");
      setContent("");
      setTopFixed(false);
      fetchNotices();
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
      await api.delete(`/notice/${id}`);
      setMsg("삭제되었습니다.");
      fetchNotices();
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
    <main className="mx-auto max-w-4xl p-6">
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-bold">공지 관리</h1>
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
        className="mb-6 space-y-2 rounded bg-white p-4 shadow-sm"
      >
        <input
          required
          placeholder="제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full rounded border px-3 py-2"
        />
        <textarea
          required
          placeholder="내용"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={5}
          className="w-full rounded border px-3 py-2"
        />
        <label className="flex items-center gap-2 text-sm">
          <input
            type="checkbox"
            checked={topFixed}
            onChange={(e) => setTopFixed(e.target.checked)}
          />
          상단 고정
        </label>
        <button
          type="submit"
          className="w-full rounded bg-brand-600 py-2 font-semibold text-white hover:bg-brand-700"
        >
          공지 등록
        </button>
      </form>

      {loading ? (
        <p className="text-gray-500">로딩 중...</p>
      ) : (
        <ul className="space-y-2">
          {notices.map((n) => (
            <li
              key={n.id}
              className="flex items-center justify-between rounded border bg-white p-3 shadow-sm"
            >
              <Link href={`/notices/${n.id}`} className="flex-1 truncate">
                {n.topFixed && (
                  <span className="mr-2 rounded bg-amber-200 px-2 py-0.5 text-xs text-amber-800">
                    공지
                  </span>
                )}
                {n.title}
              </Link>
              <button
                onClick={() => handleDelete(n.id)}
                className="rounded border border-red-300 px-2 py-0.5 text-xs text-red-600 hover:bg-red-50"
              >
                삭제
              </button>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
