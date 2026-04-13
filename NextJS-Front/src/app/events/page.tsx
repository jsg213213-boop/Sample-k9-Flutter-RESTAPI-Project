"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { api, PageResponse } from "@/lib/api";
import { LibraryEvent } from "@/types/event";
import { useAuth } from "@/lib/auth-context";

export default function EventsPage() {
  const { member } = useAuth();
  const [events, setEvents] = useState<LibraryEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState<string | null>(null);

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const res = await api.get<PageResponse<LibraryEvent> | LibraryEvent[]>(
        "/event",
        { params: { page: 0, size: 30 } },
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
  };

  useEffect(() => {
    fetchEvents();
  }, []);

  const handleApply = async (id: number) => {
    if (!member) {
      alert("로그인이 필요합니다.");
      return;
    }
    try {
      await api.post(`/event/${id}/apply`, { memberId: member.id });
      setMsg("신청이 완료되었습니다.");
      fetchEvents();
    } catch (err: unknown) {
      let m = "신청 실패";
      if (axios.isAxiosError(err)) {
        const d = err.response?.data as { message?: string } | undefined;
        if (d?.message) m = d.message;
      }
      setMsg(m);
    }
  };

  return (
    <main className="mx-auto max-w-4xl p-6">
      <h1 className="mb-4 text-2xl font-bold">🎉 도서관 행사</h1>

      {msg && (
        <p className="mb-4 rounded bg-blue-50 p-3 text-sm text-blue-700">
          {msg}
        </p>
      )}

      {loading ? (
        <p className="text-gray-500">로딩 중...</p>
      ) : events.length === 0 ? (
        <p className="text-gray-500">등록된 행사가 없습니다.</p>
      ) : (
        <ul className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {events.map((ev) => (
            <li
              key={ev.id}
              className="rounded border bg-white p-4 shadow-sm"
            >
              <div className="flex items-center gap-2">
                {ev.category && (
                  <span className="rounded bg-gray-100 px-2 py-0.5 text-xs">
                    {ev.category}
                  </span>
                )}
                <h2 className="font-semibold">{ev.title}</h2>
              </div>
              {ev.place && (
                <p className="mt-1 text-sm text-gray-600">📍 {ev.place}</p>
              )}
              {typeof ev.remainingSlots === "number" && (
                <p className="mt-1 text-xs text-gray-500">
                  잔여 {ev.remainingSlots} / {ev.maxParticipants ?? "-"}
                </p>
              )}
              {ev.content && (
                <p className="mt-2 line-clamp-3 text-xs text-gray-600">
                  {ev.content}
                </p>
              )}
              <button
                onClick={() => handleApply(ev.id)}
                className="mt-3 w-full rounded bg-brand-600 px-3 py-1.5 text-sm text-white hover:bg-brand-700"
              >
                신청
              </button>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
