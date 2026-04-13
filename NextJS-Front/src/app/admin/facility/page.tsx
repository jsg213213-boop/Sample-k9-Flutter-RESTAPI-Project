"use client";

import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import Link from "next/link";
import Protected from "@/components/Protected";
import { api } from "@/lib/api";

interface Apply {
  id: number;
  applicantName?: string;
  memberName?: string;
  facilityType?: string;
  phone?: string;
  participants?: number;
  status?: string;
  regDate?: string;
}

export default function AdminFacilityPage() {
  return (
    <Protected requireRole="ADMIN">
      <Inner />
    </Protected>
  );
}

function Inner() {
  const [items, setItems] = useState<Apply[]>([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState<string | null>(null);

  const fetchItems = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get<Apply[]>("/apply", {
        params: { _ts: Date.now() },
        headers: { "Cache-Control": "no-cache" },
      });
      setItems(Array.isArray(res.data) ? res.data : []);
    } catch (err: unknown) {
      if (err instanceof Error) setMsg(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchItems();
  }, [fetchItems]);

  const approve = async (id: number) => {
    try {
      await api.put(`/apply/${id}/approve`);
      setMsg("승인되었습니다.");
      fetchItems();
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) setMsg(err.message);
    }
  };

  const reject = async (id: number) => {
    try {
      await api.put(`/apply/${id}/reject`);
      setMsg("반려되었습니다.");
      fetchItems();
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) setMsg(err.message);
    }
  };

  const remove = async (id: number) => {
    if (!confirm("삭제하시겠습니까?")) return;
    try {
      await api.delete(`/apply/${id}`);
      setMsg("삭제되었습니다.");
      fetchItems();
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) setMsg(err.message);
    }
  };

  return (
    <main className="mx-auto max-w-5xl p-6">
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-bold">시설예약 관리</h1>
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
      ) : items.length === 0 ? (
        <p className="text-gray-500">예약 신청이 없습니다.</p>
      ) : (
        <table className="w-full rounded border bg-white text-sm shadow-sm">
          <thead className="bg-gray-50 text-left">
            <tr>
              <th className="p-2">ID</th>
              <th className="p-2">신청자</th>
              <th className="p-2">시설</th>
              <th className="p-2">연락처</th>
              <th className="p-2">인원</th>
              <th className="p-2">상태</th>
              <th className="p-2">동작</th>
            </tr>
          </thead>
          <tbody>
            {items.map((a) => (
              <tr key={a.id} className="border-t">
                <td className="p-2">{a.id}</td>
                <td className="p-2">
                  {a.applicantName ?? a.memberName ?? "-"}
                </td>
                <td className="p-2">{a.facilityType ?? "-"}</td>
                <td className="p-2">{a.phone ?? "-"}</td>
                <td className="p-2">{a.participants ?? "-"}</td>
                <td className="p-2">{a.status ?? "-"}</td>
                <td className="p-2 space-x-1">
                  <button
                    onClick={() => approve(a.id)}
                    className="rounded border border-green-300 px-2 py-0.5 text-xs text-green-700 hover:bg-green-50"
                  >
                    승인
                  </button>
                  <button
                    onClick={() => reject(a.id)}
                    className="rounded border border-yellow-300 px-2 py-0.5 text-xs text-yellow-700 hover:bg-yellow-50"
                  >
                    반려
                  </button>
                  <button
                    onClick={() => remove(a.id)}
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
