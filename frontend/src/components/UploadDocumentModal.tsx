import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { useToast } from "./ui/use-toast";
import { Upload, FileText, X, Loader2 } from "lucide-react";
import { createClient } from "@supabase/supabase-js";

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL;
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_ANON_KEY;
const supabase = createClient(supabaseUrl, supabaseAnonKey);

const N8N_WEBHOOK_URL = import.meta.env.VITE_N8N_WEBHOOK_URL;

interface UploadDocumentModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onDocumentAdded?: () => void;
}

interface ProcessedDocument {
  summary: string;
  anomalies: string[];
  category: string;
  documentType: string;
}

export default function UploadDocumentModal({
  open,
  onOpenChange,
  onDocumentAdded,
}: UploadDocumentModalProps) {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [documentTitle, setDocumentTitle] = useState("");
  const [uploading, setUploading] = useState(false);
  const [processing, setProcessing] = useState(false);
  const { toast } = useToast();

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      // Validate file size (max 10MB)
      if (file.size > 10 * 1024 * 1024) {
        toast({
          title: "File too large",
          description: "Please select a file smaller than 10MB",
          variant: "destructive",
        });
        return;
      }

      // Validate file type
      const allowedTypes = [
        "application/pdf",
        "image/jpeg",
        "image/png",
        "image/jpg",
      ];
      if (!allowedTypes.includes(file.type)) {
        toast({
          title: "Invalid file type",
          description: "Please upload PDF, JPG, or PNG files only",
          variant: "destructive",
        });
        return;
      }

      setSelectedFile(file);
      // Auto-fill title from filename if empty
      if (!documentTitle) {
        setDocumentTitle(file.name.replace(/\.[^/.]+$/, ""));
      }
    }
  };

  const handleRemoveFile = () => {
    setSelectedFile(null);
  };

  const handleUpload = async () => {
    if (!selectedFile || !documentTitle.trim()) {
      toast({
        title: "Missing information",
        description: "Please select a file and provide a title",
        variant: "destructive",
      });
      return;
    }

    setUploading(true);

    try {
      // Step 1: Upload file to Supabase Storage
      const userId = localStorage.getItem("userId") || "anonymous";
      const timestamp = Date.now();
      const fileExt = selectedFile.name.split(".").pop();
      const fileName = `${userId}/${timestamp}-${documentTitle.replace(
        /\s+/g,
        "_"
      )}.${fileExt}`;

      const { data: uploadData, error: uploadError } = await supabase.storage
        .from("medical-documents")
        .upload(fileName, selectedFile, {
          cacheControl: "3600",
          upsert: false,
        });

      if (uploadError) throw uploadError;

      toast({
        title: "File uploaded",
        description: "Processing document with AI...",
      });

      setProcessing(true);

      // Step 2: Get public URL
      const { data: urlData } = supabase.storage
        .from("medical-documents")
        .getPublicUrl(fileName);

      // Step 3: Send to n8n for processing
      const response = await fetch(N8N_WEBHOOK_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          documentTitle,
          fileUrl: urlData.publicUrl,
          fileName: fileName,
          userId: userId,
          uploadTimestamp: new Date().toISOString(),
        }),
      });

      if (!response.ok) throw new Error("Failed to process document");

      const processedData: ProcessedDocument = await response.json();

      // Step 4: Save to database with processed data
      // TODO: Replace with your actual API endpoint
      // await axios.post('API_BASE_URL/api/v1/documents', {
      //   title: documentTitle,
      //   fileUrl: urlData.publicUrl,
      //   fileName: fileName,
      //   category: processedData.category,
      //   documentType: processedData.documentType,
      //   summary: processedData.summary,
      //   anomalies: processedData.anomalies,
      //   userId: userId,
      // });

      toast({
        title: "Success!",
        description: `Document processed successfully. Category: ${processedData.category}, Type: ${processedData.documentType}`,
      });

      // Reset form
      setSelectedFile(null);
      setDocumentTitle("");
      onOpenChange(false);

      // Callback to refresh documents list
      onDocumentAdded?.();
    } catch (error: any) {
      console.error("Upload error:", error);
      toast({
        title: "Upload failed",
        description: error.message || "Failed to upload and process document",
        variant: "destructive",
      });
    } finally {
      setUploading(false);
      setProcessing(false);
    }
  };

  const isProcessing = uploading || processing;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[525px]">
        <DialogHeader>
          <DialogTitle>Upload Medical Document</DialogTitle>
          <DialogDescription>
            Upload your prescription or medical report. Our AI will
            automatically analyze and categorize it for you. (PDF, JPG, PNG -
            Max 10MB)
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          {/* File Upload */}
          <div className="space-y-2">
            <Label htmlFor="file">Document File *</Label>
            {!selectedFile ? (
              <div className="relative">
                <Input
                  id="file"
                  type="file"
                  accept=".pdf,.jpg,.jpeg,.png"
                  onChange={handleFileChange}
                  className="hidden"
                  disabled={isProcessing}
                />
                <Label
                  htmlFor="file"
                  className="flex items-center justify-center w-full h-32 border-2 border-dashed border-border rounded-lg cursor-pointer hover:bg-muted/50 transition-colors"
                >
                  <div className="text-center">
                    <Upload className="w-8 h-8 mx-auto mb-2 text-muted-foreground" />
                    <p className="text-sm text-muted-foreground">
                      Click to upload or drag and drop
                    </p>
                    <p className="text-xs text-muted-foreground mt-1">
                      PDF, JPG, PNG (Max 10MB)
                    </p>
                  </div>
                </Label>
              </div>
            ) : (
              <div className="flex items-center gap-3 p-3 border rounded-lg bg-muted/50">
                <FileText className="w-8 h-8 text-primary flex-shrink-0" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium truncate">
                    {selectedFile.name}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {(selectedFile.size / 1024).toFixed(2)} KB
                  </p>
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleRemoveFile}
                  className="flex-shrink-0"
                  disabled={isProcessing}
                >
                  <X className="w-4 h-4" />
                </Button>
              </div>
            )}
          </div>

          {/* Document Title */}
          <div className="space-y-2">
            <Label htmlFor="title">Document Title *</Label>
            <Input
              id="title"
              placeholder="e.g., Blood Test Results - Oct 2025"
              value={documentTitle}
              onChange={(e) => setDocumentTitle(e.target.value)}
              disabled={isProcessing}
            />
          </div>

          {processing && (
            <div className="flex items-center gap-2 p-3 bg-blue-50 border border-blue-200 rounded-lg">
              <Loader2 className="w-5 h-5 animate-spin text-blue-500" />
              <div className="flex-1">
                <p className="text-sm font-medium text-blue-900">
                  AI is analyzing your document...
                </p>
                <p className="text-xs text-blue-700">
                  Extracting information, detecting anomalies, and categorizing
                </p>
              </div>
            </div>
          )}
        </div>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={isProcessing}
          >
            Cancel
          </Button>
          <Button
            onClick={handleUpload}
            disabled={isProcessing}
            className="bg-[#00BFA5] hover:bg-[#00A892]"
          >
            {isProcessing ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                {uploading ? "Uploading..." : "Processing..."}
              </>
            ) : (
              "Upload & Process"
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
