import { useState, useEffect } from "react";
import { Plus, Loader2 } from "lucide-react";
import { Button } from "../../components/ui/button";
import UploadDocumentModal from "../../components/UploadDocumentModal";
import apiClient from "../../services/api";
import { useToast } from "../../components/ui/use-toast";

interface Document {
  id: string;
  title: string;
  fileUrl: string;
  category: string;
  documentType: string;
  summary: string;
  anomalies: string[];
  processingStatus: string;
  createdAt: string;
}

export default function Documents() {
  const [uploadModalOpen, setUploadModalOpen] = useState(false);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      console.log('=== Fetching Documents ===');
      console.log('Access Token:', localStorage.getItem('accessToken'));
      console.log('User ID:', localStorage.getItem('userId'));
      
      const response = await apiClient.get<Document[]>("/api/v1/documents");
      console.log('Documents fetched successfully:', response.data);
      
      const data = response.data;
      if (Array.isArray(data)) {
        setDocuments(data);
      } else {
        console.warn("API returned non-array data:", data);
        setDocuments([]);
      }
    } catch (error: any) {
      console.error("=== Failed to fetch documents ===");
      console.error("Error object:", error);
      console.error("Error response:", error.response?.data);
      console.error("Error status:", error.response?.status);
      console.error("Error message:", error.message);
      
      // Set empty array on error so component doesn't crash
      setDocuments([]);
      // Only show toast if it's available
      if (toast) {
        toast({
          title: "Error",
          description:
            "Failed to load documents. Make sure the backend is running on port 8080.",
          variant: "destructive",
        });
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDocuments();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleDocumentAdded = () => {
    fetchDocuments();
  };

  return (
    <div className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-bold mb-2">Medical Documents</h1>
          <p className="text-muted-foreground">
            Upload and manage your medical records with AI-powered analysis
          </p>
        </div>
        <Button
          onClick={() => setUploadModalOpen(true)}
          className="bg-[#00BFA5] hover:bg-[#00A892]"
        >
          <Plus className="w-4 h-4 mr-2" />
          Upload Document
        </Button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <Loader2 className="w-8 h-8 animate-spin" />
        </div>
      ) : Array.isArray(documents) && documents.length > 0 ? (
        <div className="grid gap-4">
          {documents.map((doc) => (
            <div key={doc.id} className="p-4 border rounded-lg">
              <h3 className="font-semibold">{doc.title}</h3>
              <p className="text-sm text-muted-foreground">
                {doc.category} - {doc.documentType}
              </p>
              <p className="text-xs mt-2">{doc.summary}</p>
              {doc.processingStatus === "PENDING" && (
                <span className="text-xs text-blue-500">Processing...</span>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center h-64 text-center">
          <p className="text-muted-foreground mb-4">
            No documents found. Upload your first medical document to get
            started!
          </p>
          <Button
            onClick={() => setUploadModalOpen(true)}
            className="bg-[#00BFA5] hover:bg-[#00A892]"
          >
            <Plus className="w-4 h-4 mr-2" />
            Upload Document
          </Button>
        </div>
      )}

      <UploadDocumentModal
        open={uploadModalOpen}
        onOpenChange={setUploadModalOpen}
        onDocumentAdded={handleDocumentAdded}
      />
    </div>
  );
}
