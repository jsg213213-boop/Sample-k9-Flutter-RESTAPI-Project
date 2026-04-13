"use client";

import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import Link from "next/link";
import Protected from "@/components/Protected";
import { api, PageResponse } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { Inquiry } from "@/types/inquiry";

export default function AdminInquiriesPage() {
  return (
    <Protected requireRole="ADMIN">
      <Inner />
    </Protected>
  );
}

function Inner() {
  const { member } = useAuth();
  const [items, setItems] = useState<Inquiry[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState<string | null>(null);
  const [replyText, setReplyText] = useState<Record<number, string>>({});

  const fetchItems = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get<PageResponse<Inquiry>>("/inquiry", {
        params: { page: 0, size: 50 },
      });
      setItems(res.data.content ?? []);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchItems();
  }, [fetchItems]);

  const handleReply = async (id: number) => {
    const text = replyText[id]?.trim();
    if (!text) return;
    try {
      await api.post(`/inquiry/${id}/reply`, {
        replyText: text,
        replier: member?.mname ?? "관리자",
        inquiryId: id,
      });
      setMsg("답변이 등록되었습니다.");
      setReplyText((p) => ({ ...p, [id]: "" }));
      fetchItems();
    } catch (err: unknown) {
      let m = "답변 실패";
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
      await api.delete(`/inquiry/${id}`);
      setMsg("삭제되었습니다.");
      fetchItems();
    } catch (err: unknown) {
      if (err instanceof Error) setMsg(err.message);
    }
  };

  return (
    <main className="mx-auto max-w-4xl p-6">
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-bold">문의 답변</h1>
        <Link href="/admin" className="text-sm text-brand-600">
          ← 대시보드
        </Link>
      </div>

      {msg && (
        <p className="mb-3 rounded bg-blue-50 p-3 text-sm text-blue-700">
          {msg}
        </p>
      )}

      {loading ? (
        <p className="text-gray-500">로딩 중...</p>
      ) : (
        <ul className="space-y-3">
          {items.map((inq) => (
            <li key={inq.id} className="rounded border bg-white p-4 shadow-sm">
              <div className="flex items-center justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    {inq.secret && <span className="text-xs">🔒</span>}
                    <h2 className="font-semibold">{inq.title}</h2>
                    {inq.answered && (
                      <span className="rounded bg-green-100 px-2 py-0.5 text-xs text-green-700">
                        답변완료
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-gray-500">
                    {inq.writer ?? "-"} · {inq.regDate ?? ""}
                  </p>
                </div>
                <button
                  onClick={() => handleDelete(inq.id)}
                  className="rounded border border-red-300 px-2 py-0.5 text-xs text-red-600 hover:bg-red-50"
                >
                  삭제
                </button>
              </div>
              <p className="mt-2 whitespace-pre-wrap text-sm text-gray-700">
                {inq.content}
              </p>
              <div className="mt-3 flex gap-2">
                <input
                  placeholder="답변 작성"
                  value={replyText[inq.id] ?? ""}
                  onChange={(e) =>
                    setReplyText((p) => ({ ...p, [inq.id]: e.target.value }))
                  }
                  className="flex-1 rounded border px-3 py-1.5 text-sm"
                />
                <button
                  onClick={() => handleReply(inq.id)}
                  className="rounded bg-brand-600 px-3 py-1.5 text-sm text-white hover:bg-brand-700"
                >
                  등록
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
