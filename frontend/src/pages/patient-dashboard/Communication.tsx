import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Search, Send, MessageSquare, Clock, CheckCheck } from "lucide-react";
import { useState } from "react";

export default function Communication() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedConversation, setSelectedConversation] = useState<string | null>("1");
  const [messageInput, setMessageInput] = useState("");

  const conversations = [
    {
      id: "1",
      doctor: "Dr. Sarah Johnson",
      specialty: "General Physician",
      lastMessage: "Your test results look good. Continue with the current medication.",
      timestamp: "2 hours ago",
      unread: 0,
      status: "read",
    },
    {
      id: "2",
      doctor: "Dr. Michael Chen",
      specialty: "Cardiologist",
      lastMessage: "Please schedule a follow-up appointment next week.",
      timestamp: "5 hours ago",
      unread: 2,
      status: "unread",
    },
    {
      id: "3",
      doctor: "Dr. James Anderson",
      specialty: "Neurologist",
      lastMessage: "The MRI results are ready for review.",
      timestamp: "1 day ago",
      unread: 1,
      status: "unread",
    },
    {
      id: "4",
      doctor: "Dr. Emily Peterson",
      specialty: "Laboratory",
      lastMessage: "Your blood work has been completed.",
      timestamp: "2 days ago",
      unread: 0,
      status: "read",
    },
    {
      id: "5",
      doctor: "Dr. Robert Martinez",
      specialty: "Endocrinologist",
      lastMessage: "Thank you for updating your glucose readings.",
      timestamp: "3 days ago",
      unread: 0,
      status: "read",
    },
  ];

  const messages = {
    "1": [
      {
        id: "1",
        sender: "Dr. Sarah Johnson",
        message: "Hello! I've reviewed your recent blood work results.",
        timestamp: "10:30 AM",
        isDoctor: true,
      },
      {
        id: "2",
        sender: "You",
        message: "Thank you, Doctor. Is everything okay?",
        timestamp: "10:35 AM",
        isDoctor: false,
      },
      {
        id: "3",
        sender: "Dr. Sarah Johnson",
        message: "Yes, your test results look good. Your cholesterol levels have improved significantly. Continue with the current medication and maintain your diet.",
        timestamp: "10:40 AM",
        isDoctor: true,
      },
      {
        id: "4",
        sender: "You",
        message: "That's great to hear! Should I schedule another checkup?",
        timestamp: "10:42 AM",
        isDoctor: false,
      },
      {
        id: "5",
        sender: "Dr. Sarah Johnson",
        message: "Yes, let's schedule a follow-up in 3 months to monitor your progress.",
        timestamp: "10:45 AM",
        isDoctor: true,
      },
    ],
    "2": [
      {
        id: "1",
        sender: "Dr. Michael Chen",
        message: "Hi, I wanted to discuss your recent ECG results.",
        timestamp: "Yesterday, 3:00 PM",
        isDoctor: true,
      },
      {
        id: "2",
        sender: "Dr. Michael Chen",
        message: "Please schedule a follow-up appointment next week.",
        timestamp: "Yesterday, 3:05 PM",
        isDoctor: true,
      },
    ],
    "3": [
      {
        id: "1",
        sender: "Dr. James Anderson",
        message: "The MRI results are ready for review.",
        timestamp: "2 days ago, 11:00 AM",
        isDoctor: true,
      },
    ],
    "4": [
      {
        id: "1",
        sender: "Dr. Emily Peterson",
        message: "Your blood work has been completed. All values are within normal range.",
        timestamp: "3 days ago, 9:00 AM",
        isDoctor: true,
      },
    ],
    "5": [
      {
        id: "1",
        sender: "You",
        message: "I've been tracking my glucose levels as requested.",
        timestamp: "4 days ago, 2:00 PM",
        isDoctor: false,
      },
      {
        id: "2",
        sender: "Dr. Robert Martinez",
        message: "Thank you for updating your glucose readings. They look stable.",
        timestamp: "4 days ago, 4:30 PM",
        isDoctor: true,
      },
    ],
  };

  const filteredConversations = conversations.filter((conv) =>
    conv.doctor.toLowerCase().includes(searchQuery.toLowerCase()) ||
    conv.specialty.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const currentMessages = selectedConversation ? messages[selectedConversation as keyof typeof messages] || [] : [];
  const currentConversation = conversations.find((c) => c.id === selectedConversation);

  return (
    <div className="p-8 space-y-6">
      <div>
        <h1 className="text-3xl font-bold mb-2">Communication</h1>
        <p className="text-muted-foreground">
          Message your healthcare providers and view conversation history
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 h-[calc(100vh-250px)]">
        {/* Conversations List */}
        <Card className="lg:col-span-1">
          <CardHeader>
            <CardTitle className="text-lg">Messages</CardTitle>
            <div className="relative mt-4">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
              <Input
                placeholder="Search conversations..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
          </CardHeader>
          <CardContent className="p-0">
            <div className="divide-y max-h-[500px] overflow-y-auto">
              {filteredConversations.map((conv) => (
                <button
                  key={conv.id}
                  onClick={() => setSelectedConversation(conv.id)}
                  className={`w-full p-4 text-left hover:bg-muted transition-colors ${
                    selectedConversation === conv.id ? "bg-muted" : ""
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <Avatar className="w-10 h-10">
                      <AvatarFallback className="bg-primary text-primary-foreground text-sm">
                        {conv.doctor.split(" ").map((n) => n[0]).join("")}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between mb-1">
                        <h4 className="font-semibold text-sm truncate">{conv.doctor}</h4>
                        {conv.unread > 0 && (
                          <Badge className="bg-red-500 hover:bg-red-600 text-xs">
                            {conv.unread}
                          </Badge>
                        )}
                      </div>
                      <p className="text-xs text-muted-foreground mb-1">{conv.specialty}</p>
                      <p className="text-xs text-muted-foreground truncate">{conv.lastMessage}</p>
                      <div className="flex items-center gap-1 mt-1">
                        <Clock className="w-3 h-3 text-muted-foreground" />
                        <span className="text-xs text-muted-foreground">{conv.timestamp}</span>
                      </div>
                    </div>
                  </div>
                </button>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Message Thread */}
        <Card className="lg:col-span-2">
          {selectedConversation && currentConversation ? (
            <>
              <CardHeader className="border-b">
                <div className="flex items-center gap-3">
                  <Avatar className="w-10 h-10">
                    <AvatarFallback className="bg-primary text-primary-foreground">
                      {currentConversation.doctor.split(" ").map((n) => n[0]).join("")}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <h3 className="font-semibold">{currentConversation.doctor}</h3>
                    <p className="text-sm text-muted-foreground">{currentConversation.specialty}</p>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-6">
                <div className="space-y-4 mb-4 max-h-[350px] overflow-y-auto">
                  {currentMessages.map((msg) => (
                    <div
                      key={msg.id}
                      className={`flex ${msg.isDoctor ? "justify-start" : "justify-end"}`}
                    >
                      <div
                        className={`max-w-[70%] rounded-lg p-3 ${
                          msg.isDoctor
                            ? "bg-muted"
                            : "bg-[#00BFA5] text-white"
                        }`}
                      >
                        <p className="text-sm mb-1">{msg.message}</p>
                        <div className="flex items-center gap-1 justify-end">
                          <span className={`text-xs ${msg.isDoctor ? "text-muted-foreground" : "text-white/80"}`}>
                            {msg.timestamp}
                          </span>
                          {!msg.isDoctor && <CheckCheck className="w-3 h-3" />}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Message Input */}
                <div className="flex gap-2 pt-4 border-t">
                  <Textarea
                    placeholder="Type your message..."
                    value={messageInput}
                    onChange={(e) => setMessageInput(e.target.value)}
                    className="resize-none"
                    rows={2}
                  />
                  <Button className="bg-[#00BFA5] hover:bg-[#00A892]">
                    <Send className="w-4 h-4" />
                  </Button>
                </div>
              </CardContent>
            </>
          ) : (
            <CardContent className="p-12 text-center">
              <MessageSquare className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
              <h3 className="text-lg font-semibold mb-2">No conversation selected</h3>
              <p className="text-muted-foreground">
                Select a conversation from the list to view messages
              </p>
            </CardContent>
          )}
        </Card>
      </div>
    </div>
  );
}
