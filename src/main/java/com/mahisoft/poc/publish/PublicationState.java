package com.mahisoft.poc.publish;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Builder.Default
    private MediaStatus mediaStatus = MediaStatus.PENDING;
    @Builder.Default
    private InspectionStatus inspectionStatus = InspectionStatus.PENDING;
    @Builder.Default
    private SignatureStatus signatureStatus = SignatureStatus.PENDING;
    boolean publicationCanceled;

    @JsonIgnore
    public boolean isDone() {
        return isReadyForSignature() && signatureStatus == SignatureStatus.SIGNED;
    }

    @JsonIgnore
    public boolean isReadyForSignature() {
        return mediaStatus == MediaStatus.PROCESSED && inspectionStatus == InspectionStatus.DONE;
    }

    @JsonIgnore
    public boolean isSignatureCanceled() {
        return signatureStatus == SignatureStatus.CANCELED;
    }
}
