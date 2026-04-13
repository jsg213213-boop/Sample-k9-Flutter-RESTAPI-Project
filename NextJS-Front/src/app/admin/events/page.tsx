"use client";

import { useCallback, useEffect, useState, FormEvent } from "react";
import axios from "axios";
import Link from "next/link";
import Protected from "@/components/Protected";
import { api, PageResponse } from "@/lib/api";
import { LibraryEvent } from "@/types/event";

export default function AdminEventsPage() {
  return (
    <Protected requireRole="ADMIN">
      <Inner />
    </Protected>
  );
}

function Inner() {
  const [events, setEvents] = useState<LibraryEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState<string | null>(null);

  const [title, setTitle] = useState("");
  const [category, setCategory] = useState("");
  const [place, setPlace] = useState("");
  const [content, setContent] = useState("");
  const [maxParticipants, setMaxParticipants] = useState(20);

  const fetchEvents = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get<PageResponse<LibraryEvent> | LibraryEvent[]>(
        "/event",
        { params: { page: 0, size: 50 } },
      );
      const data = res.data;
      setEvents(
        Array.isArray(data)
          ? data
          : (data as PageResponse<LibraryEvent>).content ?? [],
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchEvents();
  }, [fetchEvents]);

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post("/event", {
        title,
        category: category || undefined,
        place: place || undefined,
        content: content || undefined,
        maxParticipants,
      });
      setMsg("등록되었습니다.");
      setTitle("");
      setCategory("");
      setPlace("");
      setContent("");
      setMaxParticipants(20);
      fetchEvents();
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
      await api.delete(`/event/${id}`);
      setMsg("삭제되었습니다.");
      fetchEvents();
    } catch (err: unknown) {
      if (err instanceof Error) setMsg(err.message);
    }
  };

  return (
    <main className="mx-auto max-w-4xl p-6">
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-bold">행사 관리</h1>
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
          placeholder="행사명"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <input
          placeholder="카테고리"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <input
          placeholder="장소"
          value={place}
          onChange={(e) => setPlace(e.target.value)}
          className="rounded border px-3 py-2"
        />
        <input
          type="number"
          min={1}
          placeholder="최대 인원"
          value={maxParticipants}
          onChange={(e) => setMaxParticipants(Number(e.target.value))}
          className="rounded border px-3 py-2"
        />
        <textarea
          placeholder="내용"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={2}
          className="col-span-full rounded border px-3 py-2"
        />
        <button
          type="submit"
          className="col-span-full rounded bg-brand-600 py-2 font-semibold text-white hover:bg-brand-700"
        >
          행사 등록
        </button>
      </form>

      {loading ? (
        <p className="text-gray-500">로딩 중...</p>
      ) : (
        <ul className="space-y-2">
          {events.map((ev) => (
            <li
              key={ev.id}
              className="flex items-center justify-between rounded border bg-white p-3 shadow-sm"
            >
              <div>
                <p className="font-semibold">{ev.title}</p>
                <p className="text-xs text-gray-500">
                  {ev.category ?? "-"} · {ev.place ?? "-"} · 잔여{" "}
                  {ev.remainingSlots ?? "-"}/{ev.maxParticipants ?? "-"}
                </p>
              </div>
              <button
                onClick={() => handleDelete(ev.id)}
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
