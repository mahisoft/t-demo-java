package com.mahisoft.poc.publish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationState {

    private String assetId;
    private MediaStatus mediaStatus;
    private InspectionStatus inspectionStatus;
    private SignatureStatus signatureStatus;
    boolean publicationCanceled;

    public boolean isDone() {
        return isReadyForSignature() && signatureStatus == SignatureStatus.SIGNED;
    }

    public boolean isReadyForSignature() {
        return mediaStatus == MediaStatus.PROCESSED && inspectionStatus == InspectionStatus.DONE;
    }

    public boolean isSignatureCanceled() {
        return signatureStatus == SignatureStatus.CANCELED;
    }
}
